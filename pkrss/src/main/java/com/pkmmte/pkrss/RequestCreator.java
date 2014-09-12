package com.pkmmte.pkrss;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.pkmmte.pkrss.downloader.Downloader;
import com.pkmmte.pkrss.parser.Parser;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Fluent API for building an RSS load request.
 */
public class RequestCreator {
	private final PkRSS singleton;
	private final Request.Builder data;

	protected RequestCreator(PkRSS singleton, String url) {
		this.singleton = singleton;
		this.data = new Request.Builder(url);
	}

	/**
	 * Assigns a reference tag to this request.
	 * Leaving this empty will automatically generate a tag.
	 * @param tag
	 */
	public RequestCreator tag(String tag) {
		this.data.tag(tag);
		return this;
	}

	/**
	 * Looks up a specific query on the RSS feed.
	 * The query string is automatically encoded.
	 * @param search
	 */
	public RequestCreator search(String search) {
		this.data.search(search);
		return this;
	}

	/**
	 * Threats this request as an individual article,
	 * rather than full feed. Use only if you are sure that
	 * the load URL belongs to a single article.
	 */
	public RequestCreator individual() {
		this.data.individual(true);
		return this;
	}

	/**
	 * Ignores already cached responses when making this
	 * request. Useful for refreshing feeds/articles.
	 */
	public RequestCreator skipCache() {
		this.data.skipCache(true);
		return this;
	}

	/**
	 * Loads a specific page of the RSS feed.
	 * @param page Page to load.
	 */
	public RequestCreator page(int page) {
		this.data.page(page);
		return this;
	}

	/**
	 * Loads the next page of the current RSS feed.
	 * If no page was previously loaded, this will
	 * request the first page.
	 */
	public RequestCreator nextPage() {
		Request request = data.build();
		String url = request.url;
		int page = request.page;

		if(request.search != null)
			url += "?s=" + request.search;

		Map<String, Integer> pageTracker = singleton.getPageTracker();
		if(pageTracker.containsKey(url))
			page = pageTracker.get(url);

		this.data.page(page + 1);
		return this;
	}

	/**
	 * Assigns a different callback handler to this specific request.
	 * This is useful for handling this request in a specific thread but leaving the rest
	 * as before. If you'd like to force the main thread handler, use <b>new Handler(Looper.getMainLooper());</b>
	 * @param handler Handler thread in which to run the callback for this request.
	 */
	public RequestCreator handler(Handler handler) {
		this.data.handler(new CallbackHandler(handler));
		return this;
	}

	/**
	 * Assigns a different downloader to handle this specific request.
	 * This may be useful under certain rare conditions or for miscellaneous purposes.
	 * @param downloader Custom downloader which to override the set downloader
	 * for this request and this request only.
	 */
	public RequestCreator downloader(Downloader downloader) {
		this.data.downloader(downloader);
		return this;
	}

	/**
	 * Assigns a different parser to handle this specific request.
	 * This is useful for parsing separate data such as comments feeds.
	 * @param parser Custom parser which to override the set parser
	 * for this request and this request only.
	 */
	public RequestCreator parser(Parser parser) {
		this.data.parser(parser);
		return this;
	}

	/**
	 * Adds a callback listener to this request.
	 * @param callback Callback interface to respond to.
	 */
	public RequestCreator callback(Callback callback) {
		this.data.callback(callback);
		return this;
	}

	/**
	 * Executes request and returns a full list containing all
	 * articles loaded from this request's URL.
	 * <p>
	 * If this request is marked as individual, the list will
	 * contain only 1 index. It is recommended to use getFirst()
	 * for individual requests instead.
	 */
	public List<Article> get() throws IOException {
		final Request request = data.build();
		singleton.load(request);
		return singleton.get(request.individual ? request.url + "feed/?withoutcomments=1" : request.url, request.search);
	}

	/**
	 * Executes request and returns the first Article associated
	 * with this request. This is useful for individual Article requests.
	 * <p>
	 * May return null.
	 * @return Returns the first article associated with this request.
	 */
	public Article getFirst() {
		try {
			List<Article> articleList = get();
			if(articleList == null || articleList.size() < 1)
				return null;

			return articleList.get(0);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Executes request asynchronously.
	 * <p>
	 * Be sure to add a callback to handle this.
	 */
	public void async() {
		final Request request = data.build();

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					singleton.load(request);
				} catch (IOException e) {
					// Log
					singleton.log("Error executing request " + request.tag + " asynchronously! " + e.getMessage(), Log.ERROR);

					// Callback
					if(request.callback != null && request.handler == null)
						singleton.handler.onLoadFailed(request.callback);
					else if(request.callback != null)
						request.handler.onLoadFailed(request.callback);
				}
				return null;
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}
}