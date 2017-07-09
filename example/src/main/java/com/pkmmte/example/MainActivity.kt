package com.pkmmte.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.pkmmte.pkrss.AdaptiveReference

import com.pkmmte.pkrss.Callback
import com.pkmmte.pkrss.PkRSS
import com.pkmmte.pkrss.model.RssItem
import timber.log.Timber

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
	// List adapter
	private val itemsAdapter = ItemListAdapter()

	// Remember last successful load
	private lateinit var lastUrl: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initList()
		initActions()
		loadFeed(getString(R.string.url_example))
	}

	private fun initList() {
		itemsList.addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.list_item_spacing)))
		itemsList.layoutManager = LinearLayoutManager(this)
		itemsList.adapter = itemsAdapter
	}

	private fun initActions() {
		swipeLayout.setColorSchemeResources(R.color.colorAccent)
		swipeLayout.setOnRefreshListener {
			loadFeed(lastUrl)
		}
		btnSearch.setOnClickListener {
			// TODO: Validate
			val url = editUrl.text.toString().trim()

			loadFeed(url)
		}
	}

	private fun loadFeed(url: String) {
		lastUrl = url
		swipeLayout.isRefreshing = true
		PkRSS.with(this)
				.load(url)
				.async(object: Callback {
					override fun onLoaded(items: List<RssItem>?, exception: Exception?) {
						swipeLayout.isRefreshing = false
						if (exception != null || items == null) {
							Toast.makeText(this@MainActivity, exception?.message, Toast.LENGTH_LONG).show()
							return
						}

						for (item in items) {
							Timber.w(item.toString())
						}
						itemsAdapter.update(items)
					}
				}, AdaptiveReference.Type.STRONG)
	}
}
