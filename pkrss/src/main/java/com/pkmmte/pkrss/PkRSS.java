package com.pkmmte.pkrss;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.Request;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PkRSS {
	// General public constant keys
	public static final String KEY_ARTICLE = "ARTICLE";
	public static final String KEY_ARTICLE_ID = "ARTICLE ID";
	public static final String KEY_ARTICLE_URL = "ARTICLE URL";
	public static final String KEY_FEED_URL = "FEED URL";
	public static final String KEY_CATEGORY_NAME = "CATEGORY NAME";
	public static final String KEY_CATEGORY = "CATEGORY";
	public static final String KEY_SEARCH = "SEARCH TERM";
	public static final String KEY_READ_ARRAY = "READ ARRAY";
	public static final String KEY_FAVORITES = "FAVORITES";

	// Global singleton instance
	private static PkRSS singleton = null;

	// For issue tracking purposes
	private volatile boolean loggingEnabled;
	protected static final String TAG = "PkRSS";

	// Context is always useful for some reason.
	private final Context mContext;

	// For storing & reading read data
	private final SharedPreferences mPrefs;

	// Our handy client for getting XML feed data
	private final OkHttpClient httpClient = new OkHttpClient();
	private final String httpCacheDir = "/okhttp";
	private final int httpCacheSize = 1024 * 1024;
	private final int httpCacheMaxAge = 2 * 60 * 60;
	private final long httpConnectTimeout = 15;
	private final long httpReadTimeout = 45;

	// Reusable XML Parser
	private final RSSParser rssParser = new RSSParser(this);

	// List of stored articles
	private final Map<String, List<Article>> articleMap = new HashMap<String, List<Article>>();

	// Keep track of pages already loaded on specific feeds
	private final Map<String, Integer> pageTracker = new HashMap<String, Integer>();

	// Persistent SparseArray for checking an article's read state
	private final SparseBooleanArray readList = new SparseBooleanArray();

	// Database storing all articles marked as favorite
	private final FavoriteDatabase favoriteDatabase;

	public static PkRSS with(Context context) {
		if(singleton == null)
			singleton = new PkRSS(context.getApplicationContext());
		return singleton;
	}

	protected static PkRSS getInstance() {
		return singleton;
	}

	protected PkRSS(Context context) {
		this.mContext = context;
		this.mPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
		getRead();
		try {
			File cacheDir = new File(context.getCacheDir().getAbsolutePath() + httpCacheDir);
			this.httpClient.setCache(new Cache(cacheDir, httpCacheSize));
			this.httpClient.setConnectTimeout(httpConnectTimeout, TimeUnit.SECONDS);
			this.httpClient.setReadTimeout(httpReadTimeout, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			Log.e(TAG, "Error configuring OkHttp client! \n" + e.getMessage());
		}
		favoriteDatabase = new FavoriteDatabase(context);
	}

	public void setLoggingEnabled(boolean enabled) {
		loggingEnabled = enabled;
	}

	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	public RequestCreator load(String url) {
		return new RequestCreator(this, url);
	}

	protected void load(String url, String search, boolean individual, boolean skipCache, int page, Callback callback)
		throws IOException {
		// Can't load empty URLs, do nothing
		if(url == null || url.isEmpty()) {
			log("Invalid URL!", Log.ERROR);
			return;
		}

		// Don't load if URL is the favorites key
		if(url.equals(KEY_FAVORITES)) {
			log("Favorites URL detected, skipping load...");
			return;
		}

		// Call the callback, if available
		if(callback != null)
			callback.OnPreLoad();

		// Start tracking load time
		long time = System.currentTimeMillis();

		if(individual) // Append feed URL if individual article
			url += "feed/?withoutcomments=1";
		else if(search != null) // Append search query if available and not individual
			url += "?s=" + Uri.encode(search);

		// Create a copy for pagination & handle cache
		String requestUrl = url;
		int maxCacheAge = skipCache ? 0 : httpCacheMaxAge;

		// Put the page index into the request's HashMap
		pageTracker.put(requestUrl, page);

		// Unless the request is individual or page index is invalid, build proper URL page parameters
		if(page > 1 && !individual)
			requestUrl += (search == null ? "?paged=" : "&paged=") + String.valueOf(page);

		// Build the OkHttp request
		Request request = new Request.Builder()
			.addHeader("Cache-Control", "public, max-age=" + maxCacheAge)
			.url(requestUrl)
			.build();

		// Empty response string placeholder
		String responseStr = null;

		try {
			// Execute the built request and log its data
			log("Making a request to " + requestUrl + (skipCache ? " [SKIP-CACHE]" : " [MAX-AGE " + maxCacheAge + "]"));
			Response response = httpClient.newCall(request).execute();

			if(response.cacheResponse() != null)
				log("Response retrieved from cache");

			// Convert response body to a string
			responseStr = response.body().string();
			log("Request took " + (System.currentTimeMillis() - time) + "ms");
		}
		catch (Exception e) {
			log("Error executing/reading http request!", Log.ERROR);
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

		// Create InputStream from response
		InputStream inputStream = new ByteArrayInputStream(responseStr.getBytes());

		// Parse articles from response and inset into global list
		List<Article> newArticles = rssParser.parse(inputStream);
		insert(url, newArticles);

		// Call the callback, if available
		if(callback != null)
			callback.OnLoaded(newArticles);
	}

	public Map<String, List<Article>> get() {
		return articleMap;
	}

	public List<Article> get(String url) {
		if(url.equals(KEY_FAVORITES))
			return getFavorites();

		return articleMap.get(url);
	}

	public List<Article> get(String url, String search) {
		if(search == null)
			return articleMap.get(url);

		return articleMap.get(url + "?s=" + Uri.encode(search));
	}

	public Article get(int id) {
		long time = System.currentTimeMillis();

		// Look for an article with this id in the Article HashMap
		for(List<Article> articleList : articleMap.values()) {
			for(Article article : articleList) {
				if(article.getId() == id) {
					log("get(" + id + ") took " + (System.currentTimeMillis() - time) + "ms");
					return article;
				}
			}
		}

		// If none was found, try searching in the favorites database
		for(Article article : favoriteDatabase.getAll()) {
			if(article.getId() == id) {
				log("get(" + id + ") took " + (System.currentTimeMillis() - time) + "ms");
				return article;
			}
		}

		log("Could not find Article with id " + id, Log.WARN);
		log("get(" + id + ") took " + (System.currentTimeMillis() - time) + "ms");
		return null;
	}

	public List<Article> getFavorites() {
		return favoriteDatabase == null ? null : favoriteDatabase.getAll();
	}

	public void markAllRead(boolean read) {
		long time = System.currentTimeMillis();

		// Just clear the array and return, if marking as unread
		if(!read) {
			readList.clear();
			writeRead();
			log("markAllRead(" + String.valueOf(read) + ") took " + (System.currentTimeMillis()
				- time) + "ms");
			return;
		}

		// Look for an article with this id in the Article HashMap
		for(List<Article> articleList : articleMap.values()) {
			for(Article article : articleList)
				readList.put(article.getId(), read);
		}

		// If none was found, try searching in the favorites database
		for(Article article : favoriteDatabase.getAll()) {
			readList.put(article.getId(), read);
		}

		writeRead();
		log("markAllRead(" + String.valueOf(read) + ") took " + (System.currentTimeMillis() - time) + "ms");
	}

	public void markRead(int id, boolean read) {
		readList.put(id, read);
		writeRead();
	}

	public boolean isRead(int id) {
		return readList.get(id, false);
	}

	// TODO TODO TODO
	public boolean saveFavorite(int id) {
		return saveFavorite(get(id), true);
	}

	public boolean saveFavorite(int id, boolean favorite) {
		return saveFavorite(get(id), favorite);
	}

	public boolean saveFavorite(Article article) {
		return saveFavorite(article, true);
	}

	public boolean saveFavorite(Article article, boolean favorite) {
		long time = System.currentTimeMillis();
		log("Adding article " + article.getId() + " to favorites...");
		if(favorite)
			favoriteDatabase.add(article);
		else
			favoriteDatabase.delete(article);

		log("Saving article " + article.getId() + " to favorites took " + (System.currentTimeMillis() - time) + "ms");
		return true;
	}

	public void deleteAllFavorites() {
		long time = System.currentTimeMillis();
		log("Deleting all favorites...");
		favoriteDatabase.deleteAll();
		log("Deleting all favorites took " + (System.currentTimeMillis() - time) + "ms");
	}

	public boolean containsFavorite(int id) {
		if(favoriteDatabase == null)
			return false;

		return favoriteDatabase.contains(id);
	}

	public boolean clearCache() {
		try {
			httpClient.getCache().delete();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean clearData() {
		try {
			httpClient.getCache().delete();
			deleteAllFavorites();
			markAllRead(false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	protected Map<String, Integer> getPageTracker() {
		return pageTracker;
	}

	private void insert(String url, List<Article> newArticles) {
		if(!articleMap.containsKey(url))
			articleMap.put(url, new ArrayList<Article>());

		List<Article> articleList = articleMap.get(url);
		articleList.addAll(newArticles);

		log("New size for " + url + " is " + articleList.size());
	}

	private void getRead() {
		// Execute on background thread as we don't know how large this is
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				int size = mPrefs.getInt("READ_ARRAY_SIZE", 0);
				boolean value;
				if(size < 1)
					return null;

				for(int i = 0, key; i < size; i++) {
					key = mPrefs.getInt("READ_ARRAY_KEY_" + i, 0);
					value = mPrefs.getBoolean("READ_ARRAY_VALUE_" + i, false);

					readList.put(key, value);
				}
				return null;
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	private void writeRead() {
		// Execute on background thread as we don't know how large this is
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// Get editor & basic variables
				SharedPreferences.Editor editor = mPrefs.edit();
				int size = readList.size();
				boolean value;

				editor.putInt("READ_ARRAY_SIZE", size);
				for(int i = 0, key; i < size; i++) {
					key = readList.keyAt(i);
					value = readList.get(key);

					editor.putInt("READ_ARRAY_KEY_" + i, key);
					editor.putBoolean("READ_ARRAY_VALUE_" + i, value);
				}
				editor.commit();

				return null;
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	protected void log(String message) {
		log(TAG, message, Log.DEBUG);
	}

	protected void log(String tag, String message) {
		log(tag, message, Log.DEBUG);
	}

	protected void log(String message, int type) {
		log(TAG, message, type);
	}

	protected void log(String tag, String message, int type) {
		if(!loggingEnabled)
			return;

		switch(type) {
			case Log.VERBOSE:
				Log.v(tag, message);
				break;
			case Log.DEBUG:
				Log.d(tag, message);
				break;
			case Log.INFO:
				Log.i(tag, message);
				break;
			case Log.WARN:
				Log.w(tag, message);
				break;
			case Log.ERROR:
				Log.e(tag, message);
				break;
			case Log.ASSERT:
			default:
				Log.wtf(tag, message);
				break;
		}
	}
}