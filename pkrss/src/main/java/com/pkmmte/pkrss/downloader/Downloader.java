package com.pkmmte.pkrss.downloader;

import android.util.Log;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.pkrss.Request;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Downloader {
	// For logging purposes
	final String TAG = "Downloader";
	PkRSS singleton;

	public abstract boolean clearCache();
	public abstract String execute(Request request) throws IllegalArgumentException, IOException;
	public abstract String toSafeUrl(Request request);
	public abstract String toUrl(Request request);

	/**
	 * Attaches a {@link PkRSS} singleton instance to this Parser for logging purposes.
	 * @param singleton Singleton instance to attach to this Parser
	 */
	public final void attachInstance(PkRSS singleton) {
		this.singleton = singleton;
	}

	final void log(String message) {
		log(TAG, message, Log.DEBUG);
	}

	final void log(String tag, String message) {
		log(tag, message, Log.DEBUG);
	}

	final void log(String message, int type) {
		log(TAG, message, type);
	}

	final void log(String tag, String message, int type) {
		if(singleton == null || !singleton.isLoggingEnabled())
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