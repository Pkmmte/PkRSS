package com.pkmmte.pkrss;

import java.util.List;

public interface Callback {
	void onPreload();
	void onLoaded(List<Article> newArticles);
	void onLoadFailed();
}