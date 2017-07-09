package com.pkmmte.example

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pkmmte.pkrss.model.RssItem

import kotlinx.android.synthetic.main.list_item.view.*

/**
 * @author Pkmmte Xeleon
 */
class ItemListAdapter : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {
	// Data set
	private val items = ArrayList<RssItem>()

	init {
		setHasStableIds(true)
	}

	fun add(items: Collection<RssItem>): ItemListAdapter {
		this.items.addAll(items)
		return this
	}

	fun clear(): ItemListAdapter {
		items.clear()
		return this
	}

	fun update(items: Collection<RssItem>): ItemListAdapter {
		clear().add(items)
		notifyDataSetChanged()
		return this
	}

	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
		holder?.bind(items[position])
	}

	override fun getItemCount() = items.size

	override fun getItemId(position: Int) = items[position].id

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		// Views
		val imgHeader: ImageView = itemView.imgHeader
		val txtTitle: TextView = itemView.txtTitle
		val txtAuthor: TextView = itemView.txtAuthor
		val txtDate: TextView = itemView.txtDate

		fun bind(item: RssItem) {
			txtTitle.text = item.title
			txtAuthor.text = itemView.context.getString(R.string.by_author, item.author)
			txtDate.text = DateUtils.getRelativeTimeSpanString(item.date ?: 0, System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_ALL)
		}
	}
}
