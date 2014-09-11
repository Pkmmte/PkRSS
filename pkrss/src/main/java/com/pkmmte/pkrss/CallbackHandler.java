package com.pkmmte.pkrss;

import android.os.Handler;
import java.util.List;

/**
 * A wrapper for Android's {@link Handler} class.
 * This provides clean and easy access to interface calls
 * without having to surround code every time.
 */
class CallbackHandler {
	private Handler handler;

	CallbackHandler() {
		this(null);
	}

	CallbackHandler(Handler handler) {
		this.handler = handler;
	}

	protected void onPreload(final Callback callback) {
		if(handler == null) {
			callback.OnPreLoad();
			return;
		}

		this.handler.post(new Runnable() {
			@Override
			public void run() {
				callback.OnPreLoad();
			}
		});
	}

	protected void onLoaded(final Callback callback, final List<Article> newArticles) {
		if(handler == null) {
			callback.OnLoaded(newArticles);
			return;
		}

		this.handler.post(new Runnable() {
			@Override
			public void run() {
				callback.OnLoaded(newArticles);
			}
		});
	}

	protected void onLoadFailed(final Callback callback) {
		if(handler == null) {
			callback.OnLoadFailed();
			return;
		}

		this.handler.post(new Runnable() {
			@Override
			public void run() {
				callback.OnLoadFailed();
			}
		});
	}
}
