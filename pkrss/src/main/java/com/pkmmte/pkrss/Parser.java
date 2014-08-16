package com.pkmmte.pkrss;

import java.util.List;

public abstract class Parser {
	final String TAG = "Parser";
	final PkRSS singleton;

	public Parser(PkRSS singleton) {
		this.singleton = singleton;
	}

	public abstract List<Article> parse(String rssStream);
}