package com.pkmmte.pkrss.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Created on June 29, 2017
 *
 * @author Pkmmte Xeleon
 */
data class RssItem(
		// Unique identifier
		val id: Long,

		// Primary data
		val title: String,
		val image: String? = null,
		val description: String? = null,
		val date: Long? = 0,
		val content: String? = null,
		val author: String? = null,

        // Secondary data
		val tags: List<String>? = ArrayList(),
		val source: String? = null,
		val mediaContent: List<MediaContent>? = null,
		val enclosure: Enclosure? = null,
		val comments: String? = null
) : Parcelable, Serializable {
	fun mutate(): MutableRssItem {
		return MutableRssItem(
				id = id,
				title = title,
				image = image,
				description = description,
				date = date,
				content = content,
				author = author,
				tags = ArrayList(tags),
				source = source,
				mediaContent = ArrayList(mediaContent),
				enclosure = enclosure,
				comments = comments
		)
	}

	constructor(parcel: Parcel) : this(
			parcel.readLong(),
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readValue(Long::class.java.classLoader) as? Long,
			parcel.readString(),
			parcel.readString(),
			parcel.createStringArrayList(),
			parcel.readString(),
			parcel.createTypedArrayList(MediaContent.CREATOR),
			parcel.readParcelable(Enclosure::class.java.classLoader),
			parcel.readString())

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(title)
		parcel.writeString(image)
		parcel.writeString(description)
		parcel.writeValue(date)
		parcel.writeString(content)
		parcel.writeString(author)
		parcel.writeStringList(tags)
		parcel.writeString(source)
		parcel.writeTypedList(mediaContent)
		parcel.writeParcelable(enclosure, flags)
		parcel.writeString(comments)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<RssItem> {
		override fun createFromParcel(parcel: Parcel): RssItem {
			return RssItem(parcel)
		}

		override fun newArray(size: Int): Array<RssItem?> {
			return arrayOfNulls(size)
		}
	}
}
