package com.pkmmte.pkrss.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class MediaContent implements Parcelable, Serializable {
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
	 *
	 * @return Content url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets media content's url. This is a required attribute.
	 *
	 * @param url Url of the content
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns media content's type. This is an optional attribute.
	 *
	 * @return Type of the content. May be null
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets media content's type. This is an optional attribute.
	 *
	 * @param type Type of the content
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets media file size. This is an optional attribute.
	 *
	 * @return Filesize of media item
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * Sets medias filesize. This is an optional attribute.
	 *
	 * @param fileSize Filesize of media
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * Gets the medium of the object. This is an optional attribute.
	 *
	 * @return Medium of media
	 */
	public String getMedium() {
		return medium;
	}

	/**
	 * Sets the medium. This is an optional attribute.
	 *
	 * @param medium The medium of the media object
	 */
	public void setMedium(String medium) {
		this.medium = medium;
	}

	/**
	 * Gets whether this media is the default one. This is an optional attribute.
	 *
	 * @return True if default
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/**
	 * Sets whether this media is the default media. This is an optional attribute.
	 *
	 * @param isDefault The default state
	 */
	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * Gets the expression of the media object. This is an optional attribute.
	 *
	 * @return The expression of the media object.
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the expression of the media object. This is an optional attribute.
	 *
	 * @param expression The bitrate of the object
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Gets the bitrate of the media object. This is an optional attribute.
	 *
	 * @return The bitrate of the media object.
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * Sets the bitrate of the media object. This is an optional attribute.
	 *
	 * @param bitrate The bitrate of the object
	 */
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * Gets the framerate of the media object. This is an optional attribute.
	 *
	 * @return The framerate of the media object.
	 */
	public float getFramerate() {
		return framerate;
	}

	/**
	 * Sets the framerate of the media object. This is an optional attribute.
	 *
	 * @param framerate The framerate of the object
	 */
	public void setFramerate(float framerate) {
		this.framerate = framerate;
	}

	/**
	 * Gets the samplingrate of the media object. This is an optional attribute.
	 *
	 * @return The samplingrate of the media object.
	 */
	public float getSamplingrate() {
		return samplingrate;
	}

	/**
	 * Sets the samplingrate of the media object. This is an optional attribute.
	 *
	 * @param samplingrate The samplingrate of the object
	 */
	public void setSamplingrate(float samplingrate) {
		this.samplingrate = samplingrate;
	}

	/**
	 * Gets the channels of the media object. This is an optional attribute.
	 *
	 * @return The channels of the media object.
	 */
	public int getChannels() {
		return channels;
	}

	/**
	 * Sets the channels of the media object. This is an optional attribute.
	 *
	 * @param channels The channel count of the object
	 */
	public void setChannels(int channels) {
		this.channels = channels;
	}

	/**
	 * Gets the duration of the media object. This is an optional attribute.
	 *
	 * @return The duration of the media object.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the media object. This is an optional attribute.
	 *
	 * @param duration The width of the object
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Gets the heigth of the media object. This is an optional attribute.
	 *
	 * @return The heigth of the media object.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the media object. This is an optional attribute.
	 *
	 * @param height The width of the object
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the width of the media object. This is an optional attribute.
	 *
	 * @return The width of the media object.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of the media object. This is an optional attribute.
	 *
	 * @param width The width of the object
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Gets the default language code according to RFC 3066
	 * This is an optional attribute.
	 *
	 * @return The language code
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Sets the medias primary language. Possible codes are detailed in RFC 3066.
	 * This is an optional attribute.
	 *
	 * @param lang The language code
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
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
			ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
			ObjectInput in = new ObjectInputStream(bais);

			@SuppressWarnings("unchecked")
			Vector<MediaContent> vec = (Vector<MediaContent>) in.readObject();

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
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayStream);
			objectOutputStream.writeObject(contentArray);
			objectOutputStream.close();
			array = byteArrayStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return array;
	}
}
