package com.pkmmte.pkrss;

import android.net.Uri;
import android.text.Html;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class Rss2Parser extends Parser {
	private final List<Article> articleList = new ArrayList<Article>();
	private final SimpleDateFormat dateFormat;
	private final XmlPullParser xmlParser;

	public Rss2Parser(PkRSS singleton) {
		super(singleton);

		dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());
		dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());

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
		articleList.clear();
		long time = System.currentTimeMillis();

		try {
			InputStream input = new ByteArrayInputStream(rssStream.getBytes());
			xmlParser.setInput(input, null);
			Article article = new Article();
			int eventType = xmlParser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = xmlParser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if (tagname.equalsIgnoreCase("item")) {
							// Create a new instance
							article = new Article();
						}
						else
							handleNode(tagname, article);
						break;
					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("item")) {
							// Generate ID
							article.setId(Math.abs(article.hashCode()));

							// Remove content thumbnail
							if(article.getImage() != null)
								article.setContent(article.getContent().replaceFirst("<img.+?>", ""));

							singleton.log(TAG, article.toShortString(), Log.INFO);
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
			e.printStackTrace();
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		singleton.log(TAG, "Parsing took " + (System.currentTimeMillis() - time) + "ms");

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
							return xpp.getAttributeValue(x).replaceAll("-110x52", "");
					}
				}
				eventType = xpp.next();
			}
		}
		catch (Exception e) {
			singleton.log(TAG, "Error pulling image link from description!\n" + e.getMessage(), Log.WARN);
		}

		return "";
	}
}