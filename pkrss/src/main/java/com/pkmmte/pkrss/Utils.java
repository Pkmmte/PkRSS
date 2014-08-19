package com.pkmmte.pkrss;

import android.content.Context;
import android.util.Log;
import com.pkmmte.pkrss.downloader.DefaultDownloader;
import com.pkmmte.pkrss.downloader.Downloader;
import com.pkmmte.pkrss.downloader.OkHttpDownloader;
import java.io.File;

public class Utils {
	private static final String TAG = "Utils";

	/**
	 * Deletes the specified directory. Returns true if successful, false if not.
	 *
	 * @param dir Directory to delete.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				if (!deleteDir(new File(dir, children[i])))
					return false;
			}
		}
		return dir.delete();
	}

	/**
	 * Creates a Downloader object depending on the dependencies present.
	 *
	 * @param context Application context.
	 * @return {@link OkHttpDownloader} if the OkHttp library is present, {@link DefaultDownloader} if not.
	 */
	public static Downloader createDefaultDownloader(Context context) {
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