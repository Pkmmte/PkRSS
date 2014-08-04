package com.pkmmte.pkrss;

import android.net.Uri;
import android.text.Html;
import android.util.Log;
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

class RSSParser {
	private final String TAG = "RSSParser";
	private final PkRSS singleton;
	private List<Article> articleList = new ArrayList<Article>();
	private SimpleDateFormat dateFormat;
	private XmlPullParser xmlParser;

	protected RSSParser(PkRSS singleton) {
		this.singleton = singleton;
		dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());
		dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
		initParser();
	}

	public List<Article> parse(InputStream input) {
		articleList.clear();
		long time = System.currentTimeMillis();

		try {
			xmlParser.setInput(input, null);
			Article article = new Article();
			int eventType = xmlParser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = xmlParser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if (tagname.equalsIgnoreCase("item")) {
							// create a new instance
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

	private long getParsedDate(String encodedDate) {
		try {
			return dateFormat.parse(dateFormat.format(dateFormat.parseObject(encodedDate))).getTime();
		}
		catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

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
			singleton.log(TAG, "Error pulling image link from description!\n" + e.getMessage(),
			          Log.WARN);
		}

		return "";
	}

	private void initParser() {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			xmlParser = factory.newPullParser();
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
}