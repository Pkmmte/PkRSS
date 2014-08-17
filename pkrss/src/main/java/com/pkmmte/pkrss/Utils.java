package com.pkmmte.pkrss;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.pkmmte.pkrss.downloader.DefaultDownloader;
import com.pkmmte.pkrss.downloader.Downloader;
import com.pkmmte.pkrss.downloader.OkHttpDownloader;

public class Utils {
	private static final String TAG = "Utils";
	static Downloader createDefaultDownloader(Context context) {
		boolean okUrlFactory = false;
		try {
			Class.forName("com.squareup.okhttp.OkUrlFactory");
			okUrlFactory = true;
		} catch (ClassNotFoundException ignored) {}

		boolean okHttpClient = false;
		try {
			Class.forName("com.squareup.okhttp.OkHttpClient");
			okHttpClient = true;
		} catch (ClassNotFoundException ignored) {}

		if (okHttpClient != okUrlFactory) {
			throw new RuntimeException(""
				+ "PkRSS detected an unsupported OkHttp on the classpath.\n"
				+ "To use OkHttp with this version of PkRSS, you'll need:\n"
				+ "1. com.squareup.okhttp:okhttp:1.6.0 (or newer)\n"
				+ "2. com.squareup.okhttp:okhttp-urlconnection:1.6.0 (or newer)\n"
				+ "Note that OkHttp 2.0.0+ is supported!");
		}

		Log.d(TAG, "Downloader is " + (okHttpClient ? "OkHttpDownloader" : "DefaultDownloader"));
		return okHttpClient ? new OkHttpDownloader(context) : new DefaultDownloader(context);
	}
}