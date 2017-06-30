package com.pkmmte.pkrss.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.pkmmte.pkrss.PkRSS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Main Article class for storing all parsed/downloaded data.
 */
public class Article implements Parcelable {
	private Bundle extras;
	private List<String> tags;
	private Vector<MediaContent> mediaContentVec;
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
		this.mediaContentVec = null;
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

	public Article(Bundle extras, List<String> tags, Vector<MediaContent> mediaContent, Uri source,
				   Uri image, String title, String description, String content, String comments,
				   String author, long date, int id) {
		this.extras = extras == null ? new Bundle() : extras;
		this.tags = tags == null ? new ArrayList<String>() : tags;
		this.mediaContentVec = mediaContent == null ? new Vector<MediaContent>() : mediaContent;
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
	 * @return A list with media content pairs
	 */
	public Vector<MediaContent> getMediaContent() {
		return mediaContentVec;
	}

	/**
	 * Overrides the current list of media content with a new one.
	 * @param mediaContentVec Media content list to override the current one
	 */
	public Article setMediaContent(Vector<MediaContent> mediaContentVec) {
		this.mediaContentVec = mediaContentVec;
		return this;
	}

	/**
	 * Adds a single media content item to the list
	 * @param mediaContent The media content object to add
	 */
	public Article addMediaContent(MediaContent mediaContent) {
		if(mediaContent == null)
			return this;

		if(this.mediaContentVec == null)
			this.mediaContentVec = new Vector<>();

		this.mediaContentVec.add(mediaContent);

		return this;
	}

	/**
	 * Removes a single media content item from the list
	 * @param mediaContent The media content object to remove
	 */
	public Article removeMediaContent(MediaContent mediaContent) {
		if(mediaContent == null || this.mediaContentVec == null)
			return this;

		this.mediaContentVec.remove(mediaContent);

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
	public boolean isRead(Context context) {
		return PkRSS.with(context).isRead(id);
	}

	/**
	 * Adds this article's id to the read index.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean markRead(Context context) {
		return markRead(context, true);
	}

	/**
	 * Adds this article's id to the read index.
	 * @param read Whether or not to mark it as read.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean markRead(Context context, boolean read) {
		PkRSS.with(context).markRead(id, read);
		return true;
	}

	/**
	 * Looks up the favorite database for this article's id.
	 * @return {@code true} if this article is in the favorites database,
	 * {@code false} if otherwise, instance has not yet been created, or an error occurred.
	 */
	public boolean isFavorite(Context context) {
		return PkRSS.with(context).containsFavorite(id);
	}

	/**
	 * Adds this article into the favorites database.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean saveFavorite(Context context) {
		return saveFavorite(context, true);
	}

	/**
	 * Adds this article into the favorites database.
	 * @param favorite Whether to add it or remove it.
	 * @return {@code true} if successful, {@code false} if otherwise.
	 */
	public boolean saveFavorite(Context context, boolean favorite) {
		return PkRSS.with(context).saveFavorite(this, favorite);
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

	public static class MediaContent implements Parcelable, Serializable {
		private String url;
		private int fileSize;
		private String type;
		private String medium;
		private boolean isDefault;
		private String expression;
		private int bitrate;
		private float framerate;
		private float samplingrate;
		private int channels;
		private long duration;
		private int height;
		private int width;
		private String lang;

		public MediaContent() {
		}

		public MediaContent(String url, int fileSize, String type, String medium, boolean isDefault,
							String expression, int bitrate, int framerate, int samplingrate, int channels,
							int duration, int height, int width, String lang) {
			this.url = url;
			this.fileSize = fileSize;
			this.type = type;
			this.medium = medium;
			this.isDefault = isDefault;
			this.expression = expression;
			this.bitrate = bitrate;
			this.framerate = framerate;
			this.samplingrate = samplingrate;
			this.channels = channels;
			this.duration = duration;
			this.height = height;
			this.width = width;
			this.lang = lang;
		}

		/**
		 * Returns media content's url. This is a required attribute.
		 * @return Content url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Sets media content's url. This is a required attribute.
		 * @param url Url of the content
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * Returns media content's type. This is an optional attribute.
		 * @return Type of the content. May be null
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets media content's type. This is an optional attribute.
		 * @param type Type of the content
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Gets media file size. This is an optional attribute.
		 * @return Filesize of media item
		 */
		public int getFileSize() {
			return fileSize;
		}

		/**
		 * Sets medias filesize. This is an optional attribute.
		 * @param fileSize Filesize of media
		 */
		public void setFileSize(int fileSize) {
			this.fileSize = fileSize;
		}

		/**
		 * Gets the medium of the object. This is an optional attribute.
		 * @return Medium of media
		 */
		public String getMedium() {
			return medium;
		}

		/**
		 * Sets the medium. This is an optional attribute.
		 * @param medium The medium of the media object
		 */
		public void setMedium(String medium) {
			this.medium = medium;
		}

		/**
		 * Gets whether this media is the default one. This is an optional attribute.
		 * @return True if default
		 */
		public boolean isDefault() {
			return isDefault;
		}

		/**
		 * Sets whether this media is the default media. This is an optional attribute.
		 * @param isDefault The default state
		 */
		public void setIsDefault(boolean isDefault) {
			this.isDefault = isDefault;
		}

		/**
		 * Gets the expression of the media object. This is an optional attribute.
		 * @return The expression of the media object.
		 */
		public String getExpression() {
			return expression;
		}

		/**
		 * Sets the expression of the media object. This is an optional attribute.
		 * @param expression The bitrate of the object
		 */
		public void setExpression(String expression) {
			this.expression = expression;
		}

		/**
		 * Gets the bitrate of the media object. This is an optional attribute.
		 * @return The bitrate of the media object.
		 */
		public int getBitrate() {
			return bitrate;
		}

		/**
		 * Sets the bitrate of the media object. This is an optional attribute.
		 * @param bitrate The bitrate of the object
		 */
		public void setBitrate(int bitrate) {
			this.bitrate = bitrate;
		}

		/**
		 * Gets the framerate of the media object. This is an optional attribute.
		 * @return The framerate of the media object.
		 */
		public float getFramerate() {
			return framerate;
		}

		/**
		 * Sets the framerate of the media object. This is an optional attribute.
		 * @param framerate The framerate of the object
		 */
		public void setFramerate(float framerate) {
			this.framerate = framerate;
		}

		/**
		 * Gets the samplingrate of the media object. This is an optional attribute.
		 * @return The samplingrate of the media object.
		 */
		public float getSamplingrate() {
			return samplingrate;
		}

		/**
		 * Sets the samplingrate of the media object. This is an optional attribute.
		 * @param samplingrate The samplingrate of the object
		 */
		public void setSamplingrate(float samplingrate) {
			this.samplingrate = samplingrate;
		}

		/**
		 * Gets the channels of the media object. This is an optional attribute.
		 * @return The channels of the media object.
		 */
		public int getChannels() {
			return channels;
		}

		/**
		 * Sets the channels of the media object. This is an optional attribute.
		 * @param channels The channel count of the object
		 */
		public void setChannels(int channels) {
			this.channels = channels;
		}

		/**
		 * Gets the duration of the media object. This is an optional attribute.
		 * @return The duration of the media object.
		 */
		public long getDuration() {
			return duration;
		}

		/**
		 * Sets the duration of the media object. This is an optional attribute.
		 * @param duration The width of the object
		 */
		public void setDuration(long duration) {
			this.duration = duration;
		}

		/**
		 * Gets the heigth of the media object. This is an optional attribute.
		 * @return The heigth of the media object.
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * Sets the height of the media object. This is an optional attribute.
		 * @param height The width of the object
		 */
		public void setHeight(int height) {
			this.height = height;
		}

		/**
		 * Gets the width of the media object. This is an optional attribute.
		 * @return The width of the media object.
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * Sets the width of the media object. This is an optional attribute.
		 * @param width The width of the object
		 */
		public void setWidth(int width) {
			this.width = width;
		}

		/**
		 * Gets the default language code according to RFC 3066
		 * This is an optional attribute.
		 * @return The language code
		 */
		public String getLang() {
			return lang;
		}

		/**
		 * Sets the medias primary language. Possible codes are detailed in RFC 3066.
		 * This is an optional attribute.
		 * @param lang The language code
		 */
		public void setLang(String lang) {
			this.lang = lang;
		}

		@Override public int describeContents() {
			return 0;
		}

		@Override public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(this.url);
			dest.writeInt(this.fileSize);
			dest.writeString(this.type);
			dest.writeString(this.medium);
			dest.writeByte(isDefault ? (byte) 1 : (byte) 0);
			dest.writeString(this.expression);
			dest.writeInt(this.bitrate);
			dest.writeFloat(this.framerate);
			dest.writeFloat(this.samplingrate);
			dest.writeInt(this.channels);
			dest.writeLong(this.duration);
			dest.writeInt(this.height);
			dest.writeInt(this.width);
			dest.writeString(this.lang);
		}

		protected MediaContent(Parcel in) {
			this.url = in.readString();
			this.fileSize = in.readInt();
			this.type = in.readString();
			this.medium = in.readString();
			this.isDefault = in.readByte() != 0;
			this.expression = in.readString();
			this.bitrate = in.readInt();
			this.framerate = in.readFloat();
			this.samplingrate = in.readFloat();
			this.channels = in.readInt();
			this.duration = in.readLong();
			this.height = in.readInt();
			this.width = in.readInt();
			this.lang = in.readString();
		}

		public static final Creator<MediaContent> CREATOR = new Creator<MediaContent>() {
			public MediaContent createFromParcel(Parcel source) {
				return new MediaContent(source);
			}

			public MediaContent[] newArray(int size) {
				return new MediaContent[size];
			}
		};

		public static Vector<MediaContent> fromByteArray(byte[] byteArray) {
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream (byteArray);
				ObjectInput in = new ObjectInputStream(bais);

				@SuppressWarnings("unchecked")
				Vector<MediaContent> vec = (Vector < MediaContent >)in.readObject();

				return vec;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}

		public static byte[] toByteArray(Vector<MediaContent> contentArray) {
			byte[] array = null;
			try {
				ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteArrayStream );
				objectOutputStream.writeObject(contentArray);
				objectOutputStream.close();
				array = byteArrayStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return  array;
		}
	}
}