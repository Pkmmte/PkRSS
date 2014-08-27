package com.pkmmte.pkrss.downloader;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.pkmmte.pkrss.Request;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * An improved version of the DefaultDownloader using the OkHttp library.
 * <p>
 * This Downloader class uses Square's OkHttp library for networking. It is
 * preferred over the DefaultDownloader and often performs better than the default
 * implementation although it requires you to include OkHttp as a dependency to your project.
 */
public class OkHttpDownloader extends Downloader {
	// OkHttpClient & configuration
	private final OkHttpClient client = new OkHttpClient();
	private final String cacheDir = "/okhttp";
	private final int cacheSize = 1024 * 1024;
	private final int cacheMaxAge = 2 * 60 * 60;
	private final long connectTimeout = 15;
	private final long readTimeout = 45;

	public OkHttpDownloader(Context context) {
		this.client.setConnectTimeout(connectTimeout, TimeUnit.SECONDS);
		this.client.setReadTimeout(readTimeout, TimeUnit.SECONDS);

		try {
			File cacheDir = new File(context.getCacheDir().getAbsolutePath() + this.cacheDir);
			this.client.setCache(new Cache(cacheDir, cacheSize));
		} catch (Exception e) {
			Log.e(TAG, "Error configuring Downloader cache! \n" + e.getMessage());
		}
	}

	@Override
	public boolean clearCache() {
		try {
			client.getCache().delete();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public String execute(Request request) throws IllegalArgumentException, IOException {
		// Invalid URLs are a big no no
		if (request.url == null || request.url.isEmpty()) {
			throw new IllegalArgumentException("Invalid URL!");
		}

		// Start tracking download time
		long time = System.currentTimeMillis();

		// Empty response string placeholder
		String responseStr;

		// Handle cache
		int maxCacheAge = request.skipCache ? 0 : cacheMaxAge;

		// Build proper URL
		String requestUrl = toUrl(request);

		// Build the OkHttp request
		com.squareup.okhttp.Request httpRequest = new com.squareup.okhttp.Request.Builder()
			.addHeader("Cache-Control", "public, max-age=" + maxCacheAge)
			.url(requestUrl)
			.build();

		try {
			// Execute the built request and log its data
			log("Making a request to " + requestUrl + (request.skipCache ? " [SKIP-CACHE]" : " [MAX-AGE " + maxCacheAge + "]"));
			Response response = client.newCall(httpRequest).execute();

			// Was this retrieved from cache?
			if (response.cacheResponse() != null) log("Response retrieved from cache");

			// Convert response body to a string
			responseStr = response.body().string();
			log(TAG, "Request download took " + (System.currentTimeMillis() - time) + "ms", Log.INFO);
		} catch (Exception e) {
			log("Error executing/reading http request!", Log.ERROR);
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

		return responseStr;
	}

	@Override
	public String toSafeUrl(Request request) {
		// Copy base url
		String url = request.url;

		if (request.individual) {
			// Append feed URL if individual article
			url += "feed/?withoutcomments=1";
		}
		else if (request.search != null) {
			// Append search query if available and not individual
			url += "?s=" + Uri.encode(request.search);
		}

		// Return safe url
		return url;
	}

	@Override
	public String toUrl(Request request) {
		// Copy base url
		String url = request.url;

		if (request.individual) {
			// Handle individual urls differently
			url += "feed/?withoutcomments=1";
		}
		else {
			if (request.search != null)
				url += "?s=" + Uri.encode(request.search);
			if (request.page > 1)
				url += (request.search == null ? "?paged=" : "&paged=") + String.valueOf(request.page);
		}

		// Return safe url
		return url;
	}
}