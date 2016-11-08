package com.pkmmte.pkrss;

import android.content.Context;
import android.util.Log;
import com.pkmmte.pkrss.downloader.DefaultDownloader;
import com.pkmmte.pkrss.downloader.Downloader;
import com.pkmmte.pkrss.downloader.OkHttp3Downloader;
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
	 * @return {@link OkHttp3Downloader} or {@link OkHttpDownloader} if the OkHttp library
	 *          is present, {@link DefaultDownloader} if not.
	 */
	public static Downloader createDefaultDownloader(Context context) {
		Downloader downloaderInstance = null;

		try {
			Class.forName("com.squareup.okhttp.OkHttpClient");
			downloaderInstance = new OkHttpDownloader(context);
		} catch (ClassNotFoundException ignored) {}


		try {
			Class.forName("okhttp3.OkHttpClient");
			downloaderInstance = new OkHttp3Downloader(context);
		} catch (ClassNotFoundException ignored) {}

		if (downloaderInstance == null) {
			downloaderInstance = new DefaultDownloader(context);
		}

		Log.d(TAG, "Downloader is " + downloaderInstance);
		return downloaderInstance;
	}
}