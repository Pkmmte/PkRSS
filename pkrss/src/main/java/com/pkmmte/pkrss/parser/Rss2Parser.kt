package com.pkmmte.pkrss.parser

import android.net.Uri
import android.text.Html
import android.util.Log

import com.pkmmte.pkrss.PkRSS
import com.pkmmte.pkrss.model.*

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.StringReader
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

/**
 * Custom PkRSS parser for parsing feeds using the RSS2 standard format.
 * This is the default parser. Use [PkRSS.Builder] to apply your own custom parser
 * or modify an existing one.
 */
class Rss2Parser : Parser() {
	private val itemList = ArrayList<RssItem>()
	private val dateFormat: DateFormat
	private val pattern: Pattern
	private val xmlParser: XmlPullParser

	init {
		// Initialize DateFormat object with the default date formatting
		dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
		dateFormat.timeZone = Calendar.getInstance().timeZone
		pattern = Pattern.compile("-\\d{1,4}x\\d{1,4}")

		// Initialize XmlPullParser object with a common configuration
		val factory = XmlPullParserFactory.newInstance()
		factory.isNamespaceAware = false
		xmlParser = factory.newPullParser()
	}

	override fun parse(rssStream: String): List<RssItem> {
		// Clear previous list and start timing execution time
		itemList.clear()
		val time = System.currentTimeMillis()

		// Get InputStream from String and set it to our XmlPullParser
		val input = ByteArrayInputStream(rssStream.toByteArray())
		xmlParser.setInput(input, null)

		// Reuse mutable RssItem and event holder
		var item = MutableRssItem(0, "")
		var eventType = xmlParser.eventType

		// Loop through the entire xml feed
		while (eventType != XmlPullParser.END_DOCUMENT) {
			val tagname = xmlParser.name
			when (eventType) {
				XmlPullParser.START_TAG -> {
					if (tagname.equals("item", ignoreCase = true)) {
						// Start a new instance
						item = MutableRssItem(0, "")
					} else if (tagname.equals("enclosure", ignoreCase = true)) {
						item.enclosure = Enclosure(xmlParser)
					} else if (tagname.equals("media:content", ignoreCase = true)) {
						handleMediaContent(tagname, item)
					} else {
						// Handle this node if not an entry tag
						handleNode(tagname, item)
					}
				}
				// Enclosures not readable as text by XmlPullParser in Android and will fail in handleNode, considered not a bug
				// https://code.google.com/p/android/issues/detail?id=18658
				XmlPullParser.END_TAG -> if (tagname.equals("item", ignoreCase = true)) {
					// Generate ID
					item.id = Math.abs(item.hashCode().toLong())

					// Remove content thumbnail
					if (item.image != null && item.content != null) {
						item.content = item.content?.replaceFirst("<img.+?>".toRegex(), "")
					}

					// (Optional) Log a minimized version of the toString() output
					log(TAG, item.toString(), Log.INFO)

					// Add article object to list
					itemList.add(item.immutable())
				}
				else -> {}
			}
			eventType = xmlParser.next()
		}

		// Output execution time and return list of newly parsed articles
		log(TAG, "Parsing took " + (System.currentTimeMillis() - time) + "ms")
		return itemList
	}

	/**
	 * Handles a node from the tag node and assigns it to the correct article value.
	 * @param tag The tag which to handle.
	 * *
	 * @param item RssItem object to assign the node value to.
	 * *
	 * @return True if a proper tag was given or handled. False if improper tag was given or
	 * * if an exception if triggered.
	 */
	private fun handleNode(tag: String, item: MutableRssItem): Boolean {
		try {
			if (xmlParser.next() != XmlPullParser.TEXT)
				return false

			if (tag.equals("link", ignoreCase = true))
				item.source = Uri.parse(xmlParser.text)
			else if (tag.equals("title", ignoreCase = true))
				item.title = xmlParser.text
			else if (tag.equals("description", ignoreCase = true)) {
				val encoded = xmlParser.text
				item.image = Uri.parse(pullImageLink(encoded))
				item.description = Html.fromHtml(encoded.replace("<img.+?>".toRegex(), "")).toString()
			} else if (tag.equals("content:encoded", ignoreCase = true))
				item.content = xmlParser.text.replace("[<](/)?div[^>]*[>]".toRegex(), "")
			else if (tag.equals("wfw:commentRss", ignoreCase = true))
				item.comments = xmlParser.text
			else if (tag.equals("category", ignoreCase = true))
				item.tags?.add(xmlParser.text)
			else if (tag.equals("dc:creator", ignoreCase = true))
				item.author = xmlParser.text
			else if (tag.equals("pubDate", ignoreCase = true)) {
				item.date = getParsedDate(xmlParser.text)
			}

			return true
		} catch (e: IOException) {
			e.printStackTrace()
			return false
		} catch (e: XmlPullParserException) {
			e.printStackTrace()
			return false
		}

	}

	/**
	 * Parses the media content of the entry
	 * @param tag The tag which to handle.
	 * *
	 * @param article Article object to assign the node value to.
	 */
	private fun handleMediaContent(tag: String, item: MutableRssItem) {
		val url = xmlParser.getAttributeValue(null, "url") ?: throw IllegalArgumentException("Url argument must not be null")
		val mc = MediaContent()

		item.mediaContent?.add(mc)
		mc.url = url

		if (xmlParser.getAttributeValue(null, "type") != null) {
			mc.type = xmlParser.getAttributeValue(null, "type")
		}

		if (xmlParser.getAttributeValue(null, "fileSize") != null) {
			mc.fileSize = Integer.parseInt(xmlParser.getAttributeValue(null, "fileSize"))
		}


		if (xmlParser.getAttributeValue(null, "medium") != null) {
			mc.medium = xmlParser.getAttributeValue(null, "medium")
		}

		if (xmlParser.getAttributeValue(null, "isDefault") != null) {
			mc.setIsDefault(java.lang.Boolean.parseBoolean(xmlParser.getAttributeValue(null, "isDefault")))
		}

		if (xmlParser.getAttributeValue(null, "expression") != null) {
			mc.expression = xmlParser.getAttributeValue(null, "expression")
		}

		if (xmlParser.getAttributeValue(null, "bitrate") != null) {
			mc.bitrate = Integer.parseInt(xmlParser.getAttributeValue(null, "bitrate"))
		}

		if (xmlParser.getAttributeValue(null, "framerate") != null) {
			mc.framerate = Integer.parseInt(xmlParser.getAttributeValue(null, "framerate")).toFloat()
		}

		if (xmlParser.getAttributeValue(null, "samplingrate") != null) {
			mc.samplingrate = Integer.parseInt(xmlParser.getAttributeValue(null, "samplingrate")).toFloat()
		}

		if (xmlParser.getAttributeValue(null, "channels") != null) {
			mc.channels = Integer.parseInt(xmlParser.getAttributeValue(null, "channels"))
		}

		if (xmlParser.getAttributeValue(null, "duration") != null) {
			mc.duration = Integer.parseInt(xmlParser.getAttributeValue(null, "duration")).toLong()
		}

		if (xmlParser.getAttributeValue(null, "height") != null) {
			mc.height = Integer.parseInt(xmlParser.getAttributeValue(null, "height"))
		}

		if (xmlParser.getAttributeValue(null, "width") != null) {
			mc.width = Integer.parseInt(xmlParser.getAttributeValue(null, "width"))
		}

		if (xmlParser.getAttributeValue(null, "lang") != null) {
			mc.lang = xmlParser.getAttributeValue(null, "lang")
		}
	}

	/**
	 * Converts a date in the "EEE, d MMM yyyy HH:mm:ss Z" format to a long value.
	 * @param encodedDate The encoded date which to convert.
	 * *
	 * @return A long value for the passed date String or 0 if improperly parsed.
	 */
	private fun getParsedDate(encodedDate: String): Long {
		try {
			return dateFormat.parse(dateFormat.format(dateFormat.parseObject(encodedDate))).time
		} catch (e: ParseException) {
			log(TAG, "Error parsing date " + encodedDate, Log.WARN)
			e.printStackTrace()
			return 0
		}

	}

	/**
	 * Pulls an image URL from an encoded String.

	 * @param encoded The String which to extract an image URL from.
	 * *
	 * @return The first image URL found on the encoded String. May return an
	 * * empty String if none were found.
	 */
	private fun pullImageLink(encoded: String): String {
		try {
			val factory = XmlPullParserFactory.newInstance()
			val xpp = factory.newPullParser()

			xpp.setInput(StringReader(encoded))
			var eventType = xpp.eventType
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG && "img" == xpp.name) {
					val count = xpp.attributeCount
					for (x in 0..count - 1) {
						if (xpp.getAttributeName(x).equals("src", ignoreCase = true))
							return pattern.matcher(xpp.getAttributeValue(x)).replaceAll("")
					}
				}
				eventType = xpp.next()
			}
		} catch (e: Exception) {
			log(TAG, "Error pulling image link from description!\n" + e.message, Log.WARN)
		}

		return ""
	}
}
