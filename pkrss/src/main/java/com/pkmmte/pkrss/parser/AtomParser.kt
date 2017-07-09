package com.pkmmte.pkrss.parser

import android.net.Uri
import android.text.Html
import android.util.Log
import com.pkmmte.pkrss.PkRSS
import com.pkmmte.pkrss.model.MutableRssItem
import com.pkmmte.pkrss.model.RssItem

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
 * Custom PkRSS parser for parsing feeds using the Atom format.
 * This is the default parser. Use [PkRSS.Builder] to apply your own custom parser
 * or modify an existing one.
 */
class AtomParser : Parser() {
	private val itemList = ArrayList<RssItem>()
	private val dateFormat: DateFormat
	private val pattern: Pattern
	private val xmlParser: XmlPullParser

	init {
		// Initialize DateFormat object with the default date formatting
		dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
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

		try {
			// Get InputStream from String and set it to our XmlPullParser
			val input = ByteArrayInputStream(rssStream.toByteArray())
			xmlParser.setInput(input, null)

			// Reuse Article object and event holder
			var item = MutableRssItem(0, "")
			var eventType = xmlParser.eventType

			// Loop through the entire xml feed
			while (eventType != XmlPullParser.END_DOCUMENT) {
				val tagname = xmlParser.name
				when (eventType) {
					XmlPullParser.START_TAG -> if (tagname.equals("entry", ignoreCase = true))
						// Start a new instance
						item = MutableRssItem(0, "")
					else
						// Handle this node if not an entry tag
						handleNode(tagname, item)
					XmlPullParser.END_TAG -> if (tagname.equals("entry", ignoreCase = true)) {
						// Generate ID
						item.id = Math.abs(item.hashCode()).toLong()

						// Remove content thumbnail
						if (item.image != null && item.content != null)
							item.content = item.content?.replaceFirst("<img.+?>", "")

						// (Optional) Log a minimized version of the toString() output
						log(TAG, item.toString(), Log.INFO)

						// Add article object to list
						itemList.add(item.immutable())
					}
					else -> {}
				}
				eventType = xmlParser.next()
			}
		} catch (e: IOException) {
			// Uh oh
			e.printStackTrace()
		} catch (e: XmlPullParserException) {
			// Oh noes
			e.printStackTrace()
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
			if (tag.equals("category", ignoreCase = true))
				item.tags?.add(xmlParser.getAttributeValue(null, "term"))
			else if (tag.equals("link", ignoreCase = true)) {
				val rel = xmlParser.getAttributeValue(null, "rel")
				if (rel.equals("alternate", ignoreCase = true))
					item.source = Uri.parse(xmlParser.getAttributeValue(null, "href"))
				else if (rel.equals("replies", ignoreCase = true))
					item.comments = xmlParser.getAttributeValue(null, "href")
			}

			if (xmlParser.next() != XmlPullParser.TEXT)
				return false

			if (tag.equals("title", ignoreCase = true))
				item.title = xmlParser.text
			else if (tag.equals("summary", ignoreCase = true)) {
				val encoded = xmlParser.text
				item.image = Uri.parse(pullImageLink(encoded))
				item.description = Html.fromHtml(encoded.replace("<img.+?>".toRegex(), "")).toString()
			} else if (tag.equals("content", ignoreCase = true))
				item.content = xmlParser.text.replace("[<](/)?div[^>]*[>]".toRegex(), "")
			else if (tag.equals("category", ignoreCase = true))
				item.tags?.add(xmlParser.text)
			else if (tag.equals("name", ignoreCase = true))
				item.author = xmlParser.text
			else if (tag.equals("published", ignoreCase = true)) {
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
	 * Converts a date in the "EEE, d MMM yyyy HH:mm:ss Z" format to a long value.
	 * @param encodedDate The encoded date which to convert.
	 * *
	 * @return A long value for the passed date String or 0 if improperly parsed.
	 */
	private fun getParsedDate(encodedDate: String): Long {
		try {
			return dateFormat.parse(dateFormat.format(dateFormat.parseObject(encodedDate.replace("Z$".toRegex(), "+0000")))).time
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