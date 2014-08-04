package com.pkmmte.pkrss;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Parcelable {
	private String name;
	private String url;

	public Category(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof Category)) return false;

		Category category = (Category) o;

		if (name != null ? !name.equals(category.name) : category.name != null) return false;
		if (url != null ? !url.equals(category.url) : category.url != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (url != null ? url.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Category{" +
			"name='" + name + '\'' +
			", url='" + url + '\'' +
			'}';
	}

	protected Category(Parcel in) {
		name = in.readString();
		url = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(url);
	}

	@SuppressWarnings("unused")
	public static final Creator<Category> CREATOR = new Creator<Category>() {
		@Override
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		@Override
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

	public static class ListBuilder {
		private final List<Category> categoryList;

		public ListBuilder() {
			categoryList = new ArrayList<Category>();
		}

		public ListBuilder add(String name, String url) {
			categoryList.add(new Category(name, url));
			return this;
		}

		public List<Category> build() {
			return categoryList;
		}
	}
}