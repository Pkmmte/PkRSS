package com.pkmmte.pkrss.parser;

import android.util.Log;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.PkRSS;
import java.util.List;

/**
 * Base Parser class for Parser objects.
 * <p>
 * Extend this class upon creating your own custom parser. You may
 * handle any type of data as long as you are able to make an Article
 * ArrayList out of it.
 */
public abstract class Parser {
	// For logging purposes
	final String TAG = "Parser";
	PkRSS singleton;

	/**
	 * Parses {@link Article} objects out of the passed String response.
	 * @param rssStream String response to parse items from.
	 * @return An {@link Article} {@link List} containing newly parsed items.
	 */
	public abstract List<Article> parse(String rssStream);

	/**
	 * Attaches a {@link PkRSS} singleton instance to this Parser for logging purposes.
	 * @param singleton Singleton instance to attach to this Parser
	 */
	public final void attachInstance(PkRSS singleton) {
		this.singleton = singleton;
	}

	public final void log(String message) {
		log(TAG, message, Log.DEBUG);
	}

	public final void log(String tag, String message) {
		log(tag, message, Log.DEBUG);
	}

	public final void log(String message, int type) {
		log(TAG, message, type);
	}

	public final void log(String tag, String message, int type) {
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