package com.pkmmte.pkrss.downloader;

import android.content.Context;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.util.Log;
import com.pkmmte.pkrss.Request;
import com.pkmmte.pkrss.Utils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Default Downloader object used for general purposes.
 * <p>
 * This Downloader class uses Android's built-in HttpUrlConnection for
 * networking. It is recommended to use the OkHttpDownloader instead as
 * it is more stable and potentially performs better.
 */
public class DefaultDownloader extends Downloader {
	// OkHttpClient & configuration
	private final File cacheDir;
	private final int cacheSize = 1024 * 1024;
	private final int cacheMaxAge = 2 * 60 * 60;
	private final long connectTimeout = 15000;
	private final long readTimeout = 45000;

	public DefaultDownloader(Context context)  {
		cacheDir = new File(context.getCacheDir(), "http");
		try {
			HttpResponseCache.install(cacheDir, cacheSize);
		}
		catch (IOException e) {
			Log.i(TAG, "HTTP response cache installation failed:" + e);
		}
	}

	@Override
	public boolean clearCache() {
		return Utils.deleteDir(cacheDir);
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
		URL url = new URL(requestUrl);

		// Open a connection and configure timeouts/cache
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cache-Control", "public, max-age=" + maxCacheAge);
		connection.setConnectTimeout((int) connectTimeout);
		connection.setReadTimeout((int) readTimeout);

		// Execute the request and log its data
		log("Making a request to " + requestUrl + (request.skipCache ? " [SKIP-CACHE]" : " [MAX-AGE " + maxCacheAge + "]"));
		connection.connect();

		// Read stream
		InputStream stream = null;
		try {
			// I really don't feel comfortable using InputStreams...
			stream = new BufferedInputStream(url.openStream(), 8192);
			int ch;
			StringBuffer builder = new StringBuffer();
			while ((ch = stream.read()) != -1) {
				builder.append((char) ch);
			}

			// Convert stream to a string
			responseStr = builder.toString();
			log(TAG, "Request download took " + (System.currentTimeMillis() - time) + "ms", Log.INFO);
		} catch (Exception e) {
			log("Error executing/reading http request!", Log.ERROR);
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			if (stream != null)
				stream.close();
			connection.disconnect();
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
