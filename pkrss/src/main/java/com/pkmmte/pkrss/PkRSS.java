package com.pkmmte.pkrss;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.pkmmte.pkrss.downloader.Downloader;
import com.pkmmte.pkrss.downloader.OkHttpDownloader;
import com.pkmmte.pkrss.parser.Parser;
import com.pkmmte.pkrss.parser.Rss2Parser;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
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
	private final Downloader downloader;
	private final String httpCacheDir = "/okhttp";
	private final int httpCacheSize = 1024 * 1024;
	private final int httpCacheMaxAge = 2 * 60 * 60;
	private final long httpConnectTimeout = 15;
	private final long httpReadTimeout = 45;

	// Reusable XML Parser
	private final Parser parser;

	// List of stored articles
	private final Map<String, List<Article>> articleMap = new HashMap<String, List<Article>>();

	// Keep track of pages already loaded on specific feeds
	private final Map<String, Integer> pageTracker = new HashMap<String, Integer>();

	// Persistent SparseArray for checking an article's read state
	private final SparseBooleanArray readList = new SparseBooleanArray();

	// Database storing all articles marked as favorite
	private final FavoriteDatabase favoriteDatabase;

	/**
	 * The global default {@link PkRSS} instance.
	 * <p>
	 * This instance is automatically initialized with defaults that are suitable to most
	 * implementations.
	 * <p>
	 * If these settings do not meet the requirements of your application, you can construct your own
	 * instance with full control over the configuration by using {@link PkRSS.Builder}.
	 * You may also use {@link PkRSS.Builder#buildSingleton()} to build the singleton or
	 * {@link PkRSS.Builder#replaceSingleton()} to replace the existing singleton if it differs.
	 */
	public static PkRSS with(Context context) {
		if(singleton == null)
			singleton = new Builder(context).build();
		return singleton;
	}

	/**
	 * Returns the global singleton instance. It may be null so use wisely!
	 * @return Global singleton instance. May be null.
	 */
	protected static PkRSS getInstance() {
		return singleton;
	}

	PkRSS(Context context, Downloader downloader, Parser parser, boolean loggingEnabled) {
		this.mContext = context;
		this.downloader = downloader;
		this.downloader.attachInstance(this);
		this.parser = parser;
		this.parser.attachInstance(this);
		this.loggingEnabled = loggingEnabled;
		this.mPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
		getRead();
		favoriteDatabase = new FavoriteDatabase(context);
	}

	/**
	 * Toggle whether debug logging is enabled.
	 */
	public void setLoggingEnabled(boolean enabled) {
		loggingEnabled = enabled;
		Log.d(TAG, "Logging is now " + (enabled ? "enabled" : "disabled"));
	}

	/**
	 * {@code true} if debug logging is enabled.
	 */
	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	public RequestCreator load(String url) {
		return new RequestCreator(this, url);
	}

	protected void load(final Request request) throws IOException {
		// Don't load if URL is the favorites key
		if(request.url.equals(KEY_FAVORITES)) {
			log("Favorites URL detected, skipping load...");
			return;
		}

		// Call the callback, if available
		if(request.callback != null)
			request.callback.OnPreLoad();

		// Create a copy for pagination & handle cache
		String safeUrl = downloader.toSafeUrl(request);

		// Put the page index into the request's HashMap
		pageTracker.put(safeUrl, request.page);

		// Get response from this request
		String response = downloader.execute(request);

		// Parse articles from response and inset into global list
		List<Article> newArticles = parser.parse(response);
		insert(safeUrl, newArticles);

		// Call the callback, if available
		if(request.callback != null)
			request.callback.OnLoaded(newArticles);
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

	/**
	 * Retrieves an ArrayList of articles from the Favorite Database.
	 * @return Either an Article List or null if database wasn't properly started.
	 */
	public List<Article> getFavorites() {
		return favoriteDatabase == null ? null : favoriteDatabase.getAll();
	}

	public void markAllRead(boolean read) {
		long time = System.currentTimeMillis();

		// Just clear the array and return, if marking as unread
		if(!read) {
			readList.clear();
			writeRead();
			log("markAllRead(" + String.valueOf(read) + ") took " + (System.currentTimeMillis() - time) + "ms");
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

	/**
	 * Searches the FavoriteDatabase for the specified ID.
	 * @param id Article ID which to search for.
	 * @return {@code true} if database contains it or {@code false} if otherwise
	 * or database not yet started.
	 */
	public boolean containsFavorite(int id) {
		if(favoriteDatabase == null)
			return false;

		return favoriteDatabase.contains(id);
	}

	/**
	 * Clears {@link Downloader} cache.
	 * @return {@code true} if successfully cleared or {@code false} if otherwise.
	 */
	public boolean clearCache() {
		return downloader.clearCache();
	}

	/**
	 * Clears all PkRSS data including Downloader cache, all articles in Favorite
	 * Database, and all articles marked as read.
	 * @return {@code true} if successfully cleared or {@code false} if otherwise.
	 */
	public boolean clearData() {
		if (!downloader.clearCache())
			return false;
		deleteAllFavorites();
		markAllRead(false);
		return true;
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

	protected final void log(String message) {
		log(TAG, message, Log.DEBUG);
	}

	protected final void log(String tag, String message) {
		log(tag, message, Log.DEBUG);
	}

	protected final void log(String message, int type) {
		log(TAG, message, type);
	}

	protected final void log(String tag, String message, int type) {
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

	/** Fluent API for creating {@link PkRSS} instances. */
	public static class Builder {
		private final Context context;
		private Downloader downloader;
		private Parser parser;
		private boolean loggingEnabled;

		/**
		 * Start building a new {@link PkRSS} instance.
		 */
		public Builder(Context context) {
			if (context == null)
				throw new IllegalArgumentException("Context must not be null!");
			this.context = context.getApplicationContext();
		}

		public Builder downloader(Downloader downloader) {
			this.downloader = downloader;
			return this;
		}

		/**
		 * Specifies a custom {@link Parser} Object for which to parse data with.
		 * <b>Default: </b> {@link Rss2Parser}
		 */
		public Builder parser(Parser parser) {
			this.parser = parser;
			return this;
		}

		/**
		 * Toggle whether debug logging is enabled.
		 * <b>Default: </b> {@code false}
		 */
		public Builder loggingEnabled(boolean enabled) {
			this.loggingEnabled = enabled;
			return this;
		}

		/**
		 * Creates a {@link PkRSS} instance.
		 */
		public PkRSS build() {
			if(parser == null)
				parser = new Rss2Parser();

			if(downloader == null)
				downloader = Utils.createDefaultDownloader(context);

			return new PkRSS(context, downloader, parser, loggingEnabled);
		}

		/**
		 * Creates a {@link PkRSS} instance and assigns it to the
		 * global {@link PkRSS#singleton} instance if it doesn't already exists.
		 */
		public void buildSingleton() {
			if(singleton == null)
				singleton = build();
			else
				Log.e(TAG, "Cannot buildSingleton(), a singleton already exists! Ignoring request...");
		}

		/**
		 * <b><i>Use wisely!</i></b>
		 * <p>
		 * Creates a {@link PkRSS} instance and replaces the existing
		 * global {@link PkRSS#singleton} instance if it differs.
		 * <p>
		 * <b>Do NOT use this unless you really have to!</b>
		 */
		public void replaceSingleton() {
			// TODO
		}
	}
}