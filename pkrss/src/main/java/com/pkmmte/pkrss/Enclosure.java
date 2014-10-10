package com.pkmmte.pkrss;

import android.os.Parcel;
import android.os.Parcelable;

import org.xmlpull.v1.XmlPullParser;

public class Enclosure implements Parcelable {
    private String url;
    private String length;
    private String mimeType;

    /**
     * Creates an Enclosure from the current event in an XmlPullParser.
     * @param xmlParser The XmlPullParser object.
     */
    public Enclosure(XmlPullParser xmlParser) {
        for(int i = 0; i < xmlParser.getAttributeCount(); i++) {
            String val = xmlParser.getAttributeValue(i);
            String att = xmlParser.getAttributeName(i);

            if(att.equalsIgnoreCase("url"))
                this.setUrl(val);

            else if(att.equalsIgnoreCase("length"))
                this.setLength(val);

            else if(att.equalsIgnoreCase("type"))
                this.setMimeType(val);
        }
    }

    /**
     * Creates an Enclosure from a url, the size of the attachment, and it's mime-type.
     * @param url The url of the attachment.
     * @param length The size (in bytes) of the attachment.
     * @param mimeType The mime-type of the attachment.
     */
    public Enclosure(String url, String length, String mimeType) {
        this.url = url;
        this.length = length;
        this.mimeType = mimeType;
    }

    /**
     * Creates an Enclosure from a Parcel.
     * @param in The Parcel object.
     */
    protected Enclosure(Parcel in) {
        url = in.readString();
        length = in.readString();
        mimeType = in.readString();
    }

    /**
     * @return The url of the attachment.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url of the attachment.
     * @param url The url of the attachment.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The size (in bytes) of the attachment.
     */
    public long getLength() {
        return Long.valueOf(length);
    }

    /**
     * Sets the size of the attachment.
     * @param length The size (in bytes) of the attachment.
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * @return The mime-type of the attachment.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the url of the attachment.
     * @param mimeType The mime-type of the attachment.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return A String representing the Enclosure.
     */
    @Override
    public String toString() {
        return "Enclosure{" +
                "url='" + url + '\'' +
                ", length='" + length + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(length);
        parcel.writeString(mimeType);
    }
}
