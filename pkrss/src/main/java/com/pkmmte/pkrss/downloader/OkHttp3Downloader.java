package com.pkmmte.pkrss.downloader;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.pkmmte.pkrss.Request;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OkHttp3Downloader extends Downloader {
    // OkHttpClient & configuration
    private OkHttpClient client;
    private final String cacheDir = "/okhttp";
    private final int cacheSize = 1024 * 1024;
    private final int cacheMaxAge = 2 * 60 * 60;
    private final long connectTimeout = 15;
    private final long readTimeout = 45;

    public OkHttp3Downloader(Context context) {

        File cacheDir = new File(context.getCacheDir().getAbsolutePath() + this.cacheDir);
        client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .cache(new Cache(cacheDir, cacheSize))
                .build();
    }

    @Override
    public boolean clearCache() {
        try {
            client.cache().delete();
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
        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                .addHeader("Cache-Control", "public, max-age=" + maxCacheAge)
                .url(requestUrl)
                .build();

        try {
            // Execute the built request and log its data
            log("Making a request to " + requestUrl + (request.skipCache ? " [SKIP-CACHE]" : " [MAX-AGE " + maxCacheAge + "]"));
            Response response = client.newCall(httpRequest).execute();

            // Was this retrieved from cache?
            if (response.cacheResponse() != null) {
                log("Response retrieved from cache");
            }

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
        } else if (request.search != null) {
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
        } else {
            if (request.search != null)
                url += "?s=" + Uri.encode(request.search);
            if (request.page > 1)
                url += (request.search == null ? "?paged=" : "&paged=") + String.valueOf(request.page);
        }

        // Return safe url
        return url;
    }
}