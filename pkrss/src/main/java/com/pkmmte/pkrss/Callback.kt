package com.pkmmte.pkrss

import com.pkmmte.pkrss.model.RssItem

/**
 * Created on July 8, 2017
 *
 * @author Pkmmte Xeleon
 */
interface Callback {
	fun onLoaded(items: List<RssItem>?, exception: Exception?)
}
