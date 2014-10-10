package com.pkmmte.pkrss;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Article class for storing all parsed/downloaded data.
 */
public class Article implements Parcelable {
	private Bundle extras;
	private List<String> tags;
	private Uri source;
	private Uri image;
	private String title;
	private String description;
	private String content;
	private String comments;
	private String author;
	private long date;
	private int id;
    private Enclosure enclosure;

	public Article() {
		this.extras = new Bundle();
		this.tags = new ArrayList<String>();
		this.source = null;
		this.image = null;
		this.title = null;
		this.description = null;
		this.content = null;
		this.comments = null;
		this.author = null;
		this.date = 0;
		this.id = -1;
        this.enclosure = null;
	}

	public Article(Bundle extras, List<String> tags, Uri source, Uri image, String title, String description, String content, String comments, String author, long date, int id) {
		this.extras = extras == null ? new Bundle() : extras;
		this.tags = tags == null ? new ArrayList<String>() : tags;
		this.source = source;
		this.image = image;
		this.title = title;
		this.description = description;
		this.content = content;
		this.comments = comments;
		this.author = author;
		this.date = date;
		this.id = id;
	}

	/**
	 * Returns the entry with the given key as an object.
	 * @param key A String key.
	 * @return An Object, or null.
	 */
	public Object getExtra(String key) {
		return extras.get(key);
	}

	/**
	 * Returns the value associated with the given key.
	 * @param key A String key.
	 * @return A String value, or null.
	 */
	public String getExtraString(String key) {
		return extras.getString(key);
	}

	/**
	 * Returns the value associated with the given key.
	 * @param key A String key.
	 * @return An int value, or null.
	 */
	public int getExtraInt(String key) {
		return extras.getInt(key);
	}

	/**
	 * Returns the value associated with the given key.
	 * @param key A String key.
	 * @return A boolean value, or null.
	 */
	public boolean getExtraBoolean(String key) {
		return extras.getBoolean(key);
	}

	/**
	 * @return The Bundle object used to store all extra values.
	 */
	public Bundle getExtras() {
		return extras;
	}

	/**
	 * Inserts a given value into a Bundle associated with this Article instance.
	 * @param key A String key.
	 * @param value Value to insert.
	 */
	public Article putExtra(String key, String value) {
		this.extras.putString(key, value);
		return this;
	}

	/**
	 * Inserts a given value into a Bundle associated with this Article instance.
	 * @param key A String key.
	 * @param value Value to insert.
	 */
	public Article putExtra(String key, int value) {
		this.extras.putInt(key, value);
		return this;
	}

	/**
	 * Inserts a given value into a Bundle associated with this Article instance.
	 * @param key A String key.
	 * @param value Value to insert.
	 */
	public Article putExtra(String key, boolean value) {
		this.extras.putBoolean(key, value);
		return this;
	}

	/**
	 * Replaces all article extras with the passed extras Bundle.
	 * @param extras Bundle to override the current one with.
	 */
	public Article setExtras(Bundle extras) {
		this.extras = extras;
		return this;
	}

	/**
	 * @return A String List containing this article's tags.
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * Overrides the current list of tags with a new one.
	 * @param tags Tag List to override the current one with.
	 */
	public Article setTags(List<String> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * Adds a new tag to this article.
	 * @param tag String value to add as a tag.
	 */
	public Article setNewTag(String tag) {
		this.tags.add(tag);
		return this;
	}

	/**
	 * @return A Uri containing the source address of this article.
	 * (In other words, a link to this article)
	 */
	public Uri getSource() {
		return source;
	}

	/**
	 * Sets the source of the article.
	 * @param source Simple Uri object referencing the source. It may be a URL or null.
	 */
	public Article setSource(Uri source) {
		this.source = source;
		return this;
	}

	/**
	 * @return A Uri containing the main image source. It may be a URL, resource, asset, or null.
	 */
	public Uri getImage() {
		return image;
	}

	/**
	 * Sets the main article image.
	 * @param image Simple Uri object referencing the image source. It may be a URL, resource, asset, or null.
	 */
	public Article setImage(Uri image) {
		this.image = image;
		return this;
	}

	/**
	 * @return String containing the article's title. May be null.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the article's title.
	 * @param title String containing this article's title.
	 */
	public Article setTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * @return String containing the article's description. May be null.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the article's description.
	 * @param description String containing this article's description.
	 */
	public Article setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * @return String containing the article's content. May be null.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the article's content.
	 * @param content String containing this article's content.
	 */
	public Article setContent(String content) {
		this.content = content;
		return this;
	}

	/**
	 * @return String containing the source to this article's comments. May be null.
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the article's comment source.
	 * @param comments String containing the source to this article's comments.
	 */
	public Article setComments(String comments) {
		this.comments = comments;
		return this;
	}

    /**
     * @return Enclosure which contains the URL, length, and mime type of the article's enclosure
     */
    public Enclosure getEnclosure() {
        return this.enclosure;
    }

    /**
     * Sets the article's enclosure.
     * @param enclosure Enclosure which contains the URL, length, and mime type
     */
    public Article setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
        return this;
    }

	/**
	 * @return String containing the article's author. May be null.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the article's author.
	 * @param author String containing this article's author.
	 */
	public Article setAuthor(String author) {
		this.author = author;
		return this;
	}

	/**
	 * @return long containing the article's raw date.
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Sets the article's raw date.
	 * @param date String containing this article's raw date. Usually expressed in milliseconds.
	 */
	public Article setDate(long date) {
		this.date = date;
		return this;
	}

	/**
	 * @return long containing the article's id. IDs are normally generated based on the properties' hashcodes combined.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the article's id.
	 * @param id Long containing this article's id. Be sure to provide a unique id as it will be used for indexing.
	 */
	public Article setId(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Looks up the read index for this article's id.
	 * @return {@code true} if this article's id has been marked as read,
	 * {@code false} if otherwise or instance has not yet been created.
	 */
	public boolean isRead() {
		return PkRSS.getInstance() == null ? false : PkRSS.getInstance().isRead(id);
	}

	/**
	 * Adds this article's id to the read index.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean markRead() {
		return markRead(true);
	}

	/**
	 * Adds this article's id to the read index.
	 * @param read Whether or not to mark it as read.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean markRead(boolean read) {
		if (PkRSS.getInstance() == null) return false;

		PkRSS.getInstance().markRead(id, read);
		return true;
	}

	/**
	 * Looks up the favorite database for this article's id.
	 * @return {@code true} if this article is in the favorites database,
	 * {@code false} if otherwise, instance has not yet been created, or an error occurred.
	 */
	public boolean isFavorite() {
		return PkRSS.getInstance() == null ? false : PkRSS.getInstance().containsFavorite(id);
	}

	/**
	 * Adds this article into the favorites database.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean saveFavorite() {
		return saveFavorite(true);
	}

	/**
	 * Adds this article into the favorites database.
	 * @param favorite Whether to add it or remove it.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean saveFavorite(boolean favorite) {
		if (PkRSS.getInstance() == null) return false;

		return PkRSS.getInstance().saveFavorite(this, favorite);
	}

	/**
	 * Similar to {@link #toString()} but ommits the content and description.
	 * @return A small-ish String describing this article's properties.
	 */
	public String toShortString() {
		return "Article{" +
			"extras=" + extras +
			", tags=" + tags +
			", source=" + source +
			", image=" + image +
			", title='" + title + '\'' +
			", comments='" + comments + '\'' +
			", author='" + author + '\'' +
			", date=" + date +
			", id=" + id +
			'}';
	}

	@Override
	public String toString() {
		return "Article{" +
			"extras=" + extras +
			", tags=" + tags +
			", source=" + source +
			", image=" + image +
			", title='" + title + '\'' +
			", description='" + description + '\'' +
			", content='" + content + '\'' +
			", comments='" + comments + '\'' +
			", author='" + author + '\'' +
			", date=" + date +
			", id=" + id +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof Article)) return false;

		Article article = (Article) o;

		if (date != article.date) return false;
		if (id != article.id) return false;
		if (author != null ? !author.equals(article.author) : article.author != null) return false;
		if (comments != null ? !comments.equals(article.comments) : article.comments != null) return false;
		if (content != null ? !content.equals(article.content) : article.content != null) return false;
		if (description != null ? !description.equals(article.description) : article.description != null) return false;
		if (!extras.equals(article.extras)) return false;
		if (image != null ? !image.equals(article.image) : article.image != null) return false;
		if (source != null ? !source.equals(article.source) : article.source != null) return false;
		if (!tags.equals(article.tags)) return false;
		if (title != null ? !title.equals(article.title) : article.title != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = extras.hashCode();
		result = 31 * result + tags.hashCode();
		result = 31 * result + (source != null ? source.hashCode() : 0);
		result = 31 * result + (image != null ? image.hashCode() : 0);
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (content != null ? content.hashCode() : 0);
		result = 31 * result + (comments != null ? comments.hashCode() : 0);
		result = 31 * result + (author != null ? author.hashCode() : 0);
		result = 31 * result + (int) (date ^ (date >>> 32));
		result = 31 * result + id;
		return result;
	}

	protected Article(Parcel in) {
		extras = in.readBundle();
		if (in.readByte() == 0x01) {
			tags = new ArrayList<String>();
			in.readList(tags, String.class.getClassLoader());
		}
		else {
			tags = null;
		}
		source = (Uri) in.readValue(Uri.class.getClassLoader());
		image = (Uri) in.readValue(Uri.class.getClassLoader());
		title = in.readString();
		description = in.readString();
		content = in.readString();
		author = in.readString();
		date = in.readLong();
		id = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBundle(extras);
		if (tags == null) {
			dest.writeByte((byte) (0x00));
		}
		else {
			dest.writeByte((byte) (0x01));
			dest.writeList(tags);
		}
		dest.writeValue(source);
		dest.writeValue(image);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(content);
		dest.writeString(author);
		dest.writeLong(date);
		dest.writeInt(id);
	}

	@SuppressWarnings("unused")
	public static final Creator<Article> CREATOR = new Creator<Article>() {
		@Override
		public Article createFromParcel(Parcel in) {
			return new Article(in);
		}

		@Override
		public Article[] newArray(int size) {
			return new Article[size];
		}
	};
}