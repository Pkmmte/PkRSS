package com.pkmmte.pkrss;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

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

	public Object getExtra(String key) {
		return extras.get(key);
	}

	public String getExtraString(String key) {
		return extras.getString(key);
	}

	public int getExtraInt(String key) {
		return extras.getInt(key);
	}

	public boolean getExtraBoolean(String key) {
		return extras.getBoolean(key);
	}

	public Bundle getExtras() {
		return extras;
	}

	public Article putExtra(String key, String value) {
		this.extras.putString(key, value);
		return this;
	}

	public Article putExtra(String key, int value) {
		this.extras.putInt(key, value);
		return this;
	}

	public Article putExtra(String key, boolean value) {
		this.extras.putBoolean(key, value);
		return this;
	}

	public Article setExtras(Bundle extras) {
		this.extras = extras;
		return this;
	}

	public List<String> getTags() {
		return tags;
	}

	public Article setTags(List<String> tags) {
		this.tags = tags;
		return this;
	}

	public Article setNewTag(String tag) {
		this.tags.add(tag);
		return this;
	}

	public Uri getSource() {
		return source;
	}

	public Article setSource(Uri source) {
		this.source = source;
		return this;
	}

	public Uri getImage() {
		return image;
	}

	public Article setImage(Uri image) {
		this.image = image;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Article setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Article setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Article setContent(String content) {
		this.content = content;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public Article setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public Article setAuthor(String author) {
		this.author = author;
		return this;
	}

	public long getDate() {
		return date;
	}

	public Article setDate(long date) {
		this.date = date;
		return this;
	}

	public int getId() {
		return id;
	}

	public Article setId(int id) {
		this.id = id;
		return this;
	}

	public boolean isRead() {
		return PkRSS.getInstance() == null ? false : PkRSS.getInstance().isRead(id);
	}

	public boolean markRead() {
		return markRead(true);
	}

	public boolean markRead(boolean read) {
		if (PkRSS.getInstance() == null) return false;

		PkRSS.getInstance().markRead(id, read);
		return true;
	}

	public boolean isFavorite() {
		return PkRSS.getInstance() == null ? false : PkRSS.getInstance().containsFavorite(id);
	}

	public boolean saveFavorite() {
		return saveFavorite(true);
	}

	public boolean saveFavorite(boolean favorite) {
		if (PkRSS.getInstance() == null) return false;

		return PkRSS.getInstance().saveFavorite(this, favorite);
	}

	public String toShortString() {
		return "Article{" +
			"tags=" + tags +
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