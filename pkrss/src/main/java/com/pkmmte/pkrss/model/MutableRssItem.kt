package com.pkmmte.pkrss.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

/**
 * Created on July 7, 2017
 *
 * @author Pkmmte Xeleon
 */
data class MutableRssItem(
		// Unique identifier
		var id: Long,

		// Primary data
		var title: String,
		var image: Uri? = null,
		var description: String? = null,
		var date: Long? = 0,
		var content: String? = null,
		var author: String? = null,

		// Secondary data
		var tags: ArrayList<String>? = ArrayList(),
		var source: Uri? = null,
		var mediaContent: ArrayList<MediaContent>? = ArrayList(),
		var enclosure: Enclosure? = null,
		var comments: String? = null
) : Parcelable, Serializable {
	fun immutable(): RssItem {
		return RssItem(
				id = id,
				title = title,
				image = image,
				description = description,
				date = date,
				content = content,
				author = author,
				tags = tags,
				source = source,
				mediaContent = mediaContent,
				enclosure = enclosure,
				comments = comments
		)
	}

	constructor(parcel: Parcel) : this(
			parcel.readLong(),
			parcel.readString(),
			parcel.readParcelable(Uri::class.java.classLoader),
			parcel.readString(),
			parcel.readValue(Long::class.java.classLoader) as? Long,
			parcel.readString(),
			parcel.readString(),
			parcel.createStringArrayList(),
			parcel.readParcelable(Uri::class.java.classLoader),
			parcel.createTypedArrayList(MediaContent.CREATOR),
			parcel.readParcelable(Enclosure::class.java.classLoader),
			parcel.readString())

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(title)
		parcel.writeParcelable(image, flags)
		parcel.writeString(description)
		parcel.writeValue(date)
		parcel.writeString(content)
		parcel.writeString(author)
		parcel.writeStringList(tags)
		parcel.writeParcelable(source, flags)
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
