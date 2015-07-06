package com.pkmmte.pkrss;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.pkmmte.pkrss.downloader.Downloader;
import com.pkmmte.pkrss.parser.Parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fluent API for building an RSS load request.
 */
public class RequestCreator {
	private static final List<String> activeRequests = Collections.synchronizedList(new ArrayList<String>());

	private final PkRSS singleton;
	private final Request.Builder data;

	private long delay = 0;
	private boolean ignoreIfRunning = false;

	protected RequestCreator(PkRSS singleton, String url) {
		this.singleton = singleton;
		this.data = new Request.Builder(url);
	}

	/**
	 * Time delay before executing this request asynchronously.
	 * Defaults to 0 for immediate execution.
	 * @param delay Time in milliseconds.
	 */
	public RequestCreator delay(long delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * Indicate whether or not to ignore this request if another
	 * request with the same .tag() is already running.
	 * @param ignoreIfRunning
	 */
	public RequestCreator ignoreIfRunning(boolean ignoreIfRunning) {
		this.ignoreIfRunning = ignoreIfRunning;
		return this;
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
	 * Choose whether to handle callbacks safely.
	 * Setting to true will automatically catch any exceptions thrown.
	 * @param safe
	 */
	public RequestCreator safe(Boolean safe) {
		this.data.safe(safe);
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
		final CallbackHandler handler = request.handler != null ? request.handler : singleton.handler;
		final boolean safe = request.safe != null ? request.safe : singleton.safe;

		// Ignore current request if already running (ignoreIfRunning)
		synchronized (activeRequests) {
			if (ignoreIfRunning && activeRequests.contains(request.tag)) {
				singleton.log(request.tag + " request already running! Ignoring...");
				return;
			}
			activeRequests.add(request.tag);
		}

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					// Delay thread if specified
					if (delay > 0) {
						singleton.log("Delaying " + request.tag + " request for " + delay + "ms");
						Thread.sleep(delay);
					}

					// Execute request
					try {
						singleton.load(request);
					} catch (IOException e) {
						singleton.log("Error executing request " + request.tag + " asynchronously! " + e.getMessage(), Log.ERROR);
						handler.onLoadFailed(safe, request.callback.get());
					}
				} catch (InterruptedException e) {
					singleton.log(request.tag + " thread interrupted!");
				} finally {
					synchronized (activeRequests) {
						activeRequests.remove(request.tag);
					}
				}
				return null;
			}

			@Override
			protected void onCancelled(Void aVoid) {
				super.onCancelled(aVoid);

				// Double check to make sure this request is removed
				synchronized (activeRequests) {
					activeRequests.remove(request.tag);
				}
			}
		}.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}
}