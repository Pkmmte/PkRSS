package com.pkmmte.pkrss.parser;

import android.net.Uri;
import android.text.Html;
import android.util.Log;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Enclosure;
import com.pkmmte.pkrss.PkRSS;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Custom PkRSS parser for parsing feeds using the RSS2 standard format.
 * This is the default parser. Use {@link PkRSS.Builder} to apply your own custom parser
 * or modify an existing one.
 */
public class Rss2Parser extends Parser {
	private final List<Article> articleList = new ArrayList<Article>();
	private final DateFormat dateFormat;
	private final Pattern pattern;
	private final XmlPullParser xmlParser;

	public Rss2Parser() {
		// Initialize DateFormat object with the default date formatting
		dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());
		dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
		pattern = Pattern.compile("-\\d{1,4}x\\d{1,4}");

		// Initialize XmlPullParser object with a common configuration
		XmlPullParser parser = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			parser = factory.newPullParser();
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		xmlParser = parser;
	}

	@Override
	public List<Article> parse(String rssStream) {
		// Clear previous list and start timing execution time
		articleList.clear();
		long time = System.currentTimeMillis();

		try {
			// Get InputStream from String and set it to our XmlPullParser
			InputStream input = new ByteArrayInputStream(rssStream.getBytes());
			xmlParser.setInput(input, null);

			// Reuse Article object and event holder
			Article article = new Article();
			int eventType = xmlParser.getEventType();

			// Loop through the entire xml feed
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = xmlParser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if (tagname.equalsIgnoreCase("item")) // Start a new instance
							article = new Article();
                        // Enclosures not readable as text by XmlPullParser in Android and will fail in handleNode, considered not a bug
                        // https://code.google.com/p/android/issues/detail?id=18658
                        else if (tagname.equalsIgnoreCase("enclosure")) {
                            article.setEnclosure(new Enclosure(xmlParser));
                        }
						else // Handle this node if not an entry tag
							handleNode(tagname, article);
						break;
					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("item")) {
							// Generate ID
							article.setId(Math.abs(article.hashCode()));

							// Remove content thumbnail
							if(article.getImage() != null && article.getContent() != null)
								article.setContent(article.getContent().replaceFirst("<img.+?>", ""));

							// (Optional) Log a minimized version of the toString() output
							log(TAG, article.toShortString(), Log.INFO);

							// Add article object to list
							articleList.add(article);
						}
						break;
					default:
						break;
				}
				eventType = xmlParser.next();
			}
		}
		catch (IOException e) {
			// Uh oh
			e.printStackTrace();
		}
		catch (XmlPullParserException e) {
			// Oh noes
			e.printStackTrace();
		}

		// Output execution time and return list of newly parsed articles
		log(TAG, "Parsing took " + (System.currentTimeMillis() - time) + "ms");
		return articleList;
	}

	/**
	 * Handles a node from the tag node and assigns it to the correct article value.
	 * @param tag The tag which to handle.
	 * @param article Article object to assign the node value to.
	 * @return True if a proper tag was given or handled. False if improper tag was given or
	 * if an exception if triggered.
	 */
	private boolean handleNode(String tag, Article article) {
		try {
			if(xmlParser.next() != XmlPullParser.TEXT)
				return false;

			if (tag.equalsIgnoreCase("link"))
				article.setSource(Uri.parse(xmlParser.getText()));
			else if (tag.equalsIgnoreCase("title"))
				article.setTitle(xmlParser.getText());
			else if (tag.equalsIgnoreCase("description")) {
				String encoded = xmlParser.getText();
				article.setImage(Uri.parse(pullImageLink(encoded)));
				article.setDescription(Html.fromHtml(encoded.replaceAll("<img.+?>", "")).toString());
			}
			else if (tag.equalsIgnoreCase("content:encoded"))
				article.setContent(xmlParser.getText().replaceAll("[<](/)?div[^>]*[>]", ""));
			else if (tag.equalsIgnoreCase("wfw:commentRss"))
				article.setComments(xmlParser.getText());
			else if (tag.equalsIgnoreCase("category"))
				article.setNewTag(xmlParser.getText());
			else if (tag.equalsIgnoreCase("dc:creator"))
				article.setAuthor(xmlParser.getText());
			else if (tag.equalsIgnoreCase("pubDate")) {
				article.setDate(getParsedDate(xmlParser.getText()));
			}

			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Converts a date in the "EEE, d MMM yyyy HH:mm:ss Z" format to a long value.
	 * @param encodedDate The encoded date which to convert.
	 * @return A long value for the passed date String or 0 if improperly parsed.
	 */
	private long getParsedDate(String encodedDate) {
		try {
			return dateFormat.parse(dateFormat.format(dateFormat.parseObject(encodedDate))).getTime();
		}
		catch (ParseException e) {
			log(TAG, "Error parsing date " + encodedDate, Log.WARN);
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Pulls an image URL from an encoded String.
	 *
	 * @param encoded The String which to extract an image URL from.
	 * @return The first image URL found on the encoded String. May return an
	 * empty String if none were found.
	 */
	private String pullImageLink(String encoded) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput(new StringReader(encoded));
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG && "img".equals(xpp.getName())) {
					int count = xpp.getAttributeCount();
					for (int x = 0; x < count; x++) {
						if (xpp.getAttributeName(x).equalsIgnoreCase("src"))
							return pattern.matcher(xpp.getAttributeValue(x)).replaceAll("");
					}
				}
				eventType = xpp.next();
			}
		}
		catch (Exception e) {
			log(TAG, "Error pulling image link from description!\n" + e.getMessage(), Log.WARN);
		}

		return "";
	}
}
