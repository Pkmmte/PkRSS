package com.pkmmte.pkrss;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Article implements Parcelable {
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

	public Article(List<String> tags, Uri source, Uri image, String title, String description, String content, String comments, String author, long date, int id) {
		this.tags = tags;
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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void setNewTag(String tag) {
		this.tags.add(tag);
	}

	public Uri getSource() {
		return source;
	}

	public void setSource(Uri source) {
		this.source = source;
	}

	public Uri getImage() {
		return image;
	}

	public void setImage(Uri image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRead() {
		return PkRSS.getInstance() == null ? false : PkRSS.getInstance().isRead(id);
	}

	public boolean markRead() {
		return markRead(true);
	}

	public boolean markRead(boolean read) {
		if(PkRSS.getInstance() == null)
			return false;

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
		if(PkRSS.getInstance() == null)
			return false;

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
			"tags=" + tags +
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
		if (comments != null ? !comments.equals(article.comments) : article.comments != null)
			return false;
		if (content != null ? !content.equals(article.content) : article.content != null)
			return false;
		if (description != null ? !description.equals(article.description)
			: article.description != null) {
			return false;
		}
		if (image != null ? !image.equals(article.image) : article.image != null) return false;
		if (source != null ? !source.equals(article.source) : article.source != null) return false;
		if (!tags.equals(article.tags)) return false;
		if (title != null ? !title.equals(article.title) : article.title != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = tags.hashCode();
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
		if (in.readByte() == 0x01) {
			tags = new ArrayList<String>();
			in.readList(tags, String.class.getClassLoader());
		} else {
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
		if (tags == null) {
			dest.writeByte((byte) (0x00));
		} else {
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