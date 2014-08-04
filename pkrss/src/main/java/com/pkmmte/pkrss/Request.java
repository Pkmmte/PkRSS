package com.pkmmte.pkrss;

import java.util.concurrent.atomic.AtomicLong;

public final class Request {
	private static final AtomicLong ID_GENERATOR = new AtomicLong(System.currentTimeMillis() * 100000);

	public final String tag;
	public final String url;
	public final String search;
	public final boolean individual;
	public final boolean skipCache;
	public final int page;
	public final PkRSS.Callback callback;

	public Request(String url, String search, boolean individual, boolean skipCache, int page, PkRSS.Callback callback) {
		this.tag = String.valueOf(ID_GENERATOR.incrementAndGet());
		this.url = url;
		this.search = search;
		this.individual = individual;
		this.skipCache = skipCache;
		this.page = page;
		this.callback = callback;
	}

	public Request(Builder builder) {
		this.tag = builder.tag == null ? String.valueOf(ID_GENERATOR.incrementAndGet()) : builder.tag;
		this.url = builder.url;
		this.search = builder.search;
		this.individual = builder.individual;
		this.skipCache = builder.skipCache;
		this.page = builder.page;
		this.callback = builder.callback;
	}

	public static final class Builder {
		private String tag;
		private String url;
		private String search;
		private boolean individual;
		private boolean skipCache;
		private int page;
		private PkRSS.Callback callback;

		public Builder(String url) {
			this.tag = null;
			this.url = url;
			this.search = null;
			this.individual = false;
			this.skipCache = false;
			this.page = 1;
			this.callback = null;
		}

		public Builder tag(String tag) {
			this.tag = tag;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder search(String search) {
			this.search = search;
			return this;
		}

		public Builder individual(boolean individual) {
			this.individual = individual;
			return this;
		}

		public Builder skipCache(boolean skipCache) {
			this.skipCache = skipCache;
			return this;
		}

		public Builder page(int page) {
			this.page = page;
			return this;
		}

		public Builder callback(PkRSS.Callback callback) {
			this.callback = callback;
			return this;
		}

		public Request build() {
			return new Request(this);
		}
	}
}