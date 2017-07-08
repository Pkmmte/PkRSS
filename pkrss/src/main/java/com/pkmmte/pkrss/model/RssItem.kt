package com.pkmmte.pkrss.model

import android.net.Uri
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
		val image: Uri? = null,
		val description: String? = null,
		val date: Long? = 0,
		val content: String? = null,
		val author: String? = null,

        // Secondary data
		val tags: List<String>? = null,
		val source: Uri? = null,
		val mediaContent: List<MediaContent>? = null,
		val enclosure: Enclosure? = null,
		val comments: String? = null
) : Parcelable, Serializable {
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
