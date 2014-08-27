package com.pkmmte.pkrss.downloader;

import android.util.Log;
import com.pkmmte.pkrss.PkRSS;
import com.pkmmte.pkrss.Request;
import java.io.IOException;

/**
 * Base Downloader class for Downloader objects.
 * <p>
 * Extend this class upon creating your own custom downloader. You may
 * do anything you want with it as long as you're able to return data
 * and format URLs properly.
 */
public abstract class Downloader {
	// For logging purposes
	final String TAG = "Downloader";
	PkRSS singleton;

	/**
	 * Clears the {@link Downloader} cache.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public abstract boolean clearCache();

	/**
	 * Executes the specified request and returns the response String.
	 * @param request PkRSS Request object containing all necessary parameters.
	 * @return Response String to parse
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public abstract String execute(Request request) throws IllegalArgumentException, IOException;

	/**
	 * Parses a request into a safe URL to be used for caching/tracking purposes.
	 * <p>
	 * You'd normally want to build something similar as {@link #toUrl(Request)} with the exception
	 * of certain parameters such as pagination or extra useless data.
	 * <p>
	 * <b>Note:</b> Returning an invalid URL may cause caching errors and mishandled memory.
	 * @param request PkRSS Request object containing all necessary parameters.
	 * @return Safe URL String to be used for tracking and caching purposes.
	 */
	public abstract String toSafeUrl(Request request);

	/**
	 * Parses a request into a URL to be used for execution.
	 * @param request PkRSS Request object containing all necessary parameters.
	 * @return A URL String to load valid data from.
	 */
	public abstract String toUrl(Request request);

	/**
	 * Attaches a {@link PkRSS} singleton instance to this Downloader for logging purposes.
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