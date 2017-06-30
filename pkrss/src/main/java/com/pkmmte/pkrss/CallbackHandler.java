package com.pkmmte.pkrss;

import android.os.Handler;

import com.pkmmte.pkrss.model.Article;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A wrapper for Android's {@link Handler} class.
 * This provides clean and easy access to interface calls
 * without having to surround code every time.
 */
class CallbackHandler {
	private static final Class clazz = Callback.class;
	private Handler handler;

	CallbackHandler() {
		this(null);
	}

	CallbackHandler(Handler handler) {
		this.handler = handler;
	}

	protected void onPreload(final boolean safe, final Callback callback) {
		Method method = getDeclaredMethod(clazz, "onPreload");
		invokeCallback(method, callback, safe);
	}

	protected void onLoaded(final boolean safe, final Callback callback, final List<Article> newArticles) {
		Method method = getDeclaredMethod(clazz, "onLoaded", List.class);
		invokeCallback(method, callback, safe, newArticles);
	}

	protected void onLoadFailed(boolean safe, Callback callback) {
		Method method = getDeclaredMethod(clazz, "onLoadFailed");
		invokeCallback(method, callback, safe);
	}

	private void invokeCallback(final Method method, final Callback callback, final boolean safe, final Object... args) {
		// Catch invalid calls before proceeding
		if(callback == null)
			return;

		// Create runnable containing invoked code
		Runnable call = new Runnable() {
			@Override
			public void run() {
				try {
					method.invoke(callback, args);
				} catch (Exception e) {
					if(safe)
						PkRSS.getInstance().log("Caught " + clazz.getSimpleName() + '.' + method.getName() + " exception! [" + e.getMessage() + ']');
					else
						throw new RuntimeException(e);
				}
			}
		};

		// Execute using handler if available, otherwise use default thread
		if (handler != null)
			handler.post(call);
		else
			call.run();
	}

	private static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No method named " + name + " in class " + clazz.getSimpleName() + " [" + e.getMessage() + ']');
		}
	}
}
