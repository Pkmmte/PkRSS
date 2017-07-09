package com.pkmmte.pkrss;

import com.pkmmte.pkrss.model.RssItem;

import java.util.List;

public interface Callback {
	void onPreload();
	void onLoaded(List<RssItem> newItems);
	void onLoadFailed();
}