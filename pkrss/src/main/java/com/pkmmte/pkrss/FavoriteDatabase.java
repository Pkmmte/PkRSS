package com.pkmmte.pkrss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FavoriteDatabase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db.pkrss.favorites";
	private static final String TABLE_ARTICLES = "articles";

	private static final String KEY_TAGS = "TAGS";
	private static final String KEY_SOURCE = "SOURCE";
	private static final String KEY_IMAGE = "IMAGE";
	private static final String KEY_TITLE = "TITLE";
	private static final String KEY_DESCRIPTION = "DESCRIPTION";
	private static final String KEY_CONTENT = "CONTENT";
	private static final String KEY_COMMENTS = "COMMENTS";
	private static final String KEY_AUTHOR = "AUTHOR";
	private static final String KEY_DATE = "DATE";
	private static final String KEY_ID = "ID";

	public FavoriteDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ARTICLES_TABLE = "CREATE TABLE "
			+ TABLE_ARTICLES
			+ " ( "
			+ KEY_TAGS
			+ " TEXT , "
			+ KEY_SOURCE
			+ " TEXT , "
			+ KEY_IMAGE
			+ " TEXT ,"
			+ KEY_TITLE
			+ " TEXT ,"
			+ KEY_DESCRIPTION
			+ " TEXT , "
			+ KEY_CONTENT
			+ " TEXT , "
			+ KEY_COMMENTS
			+ " TEXT , "
			+ KEY_AUTHOR
			+ " TEXT , "
			+ KEY_DATE
			+ " TEXT , "
			+ KEY_ID
			+ " INTEGER  NOT NULL  UNIQUE "
			+ ")";
		db.execSQL(CREATE_ARTICLES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
		onCreate(db);
	}

	public void add(Article article) {
		SQLiteDatabase db = this.getWritableDatabase();

		// Insert
		ContentValues values = new ContentValues();
		values.put(KEY_TAGS, TextUtils.join("_PCX_", article.getTags()));
		values.put(KEY_SOURCE, article.getSource().toString());
		values.put(KEY_IMAGE, article.getImage().toString());
		values.put(KEY_TITLE, article.getTitle());
		values.put(KEY_DESCRIPTION, article.getDescription());
		values.put(KEY_CONTENT, article.getContent());
		values.put(KEY_COMMENTS, article.getComments());
		values.put(KEY_AUTHOR, article.getAuthor());
		values.put(KEY_DATE, article.getDate());
		values.put(KEY_ID, article.getId());

		db.insert(TABLE_ARTICLES, null, values);
		db.close();
	}

	public Article get(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_ARTICLES, new String[] { KEY_TAGS, KEY_SOURCE, KEY_IMAGE, KEY_TITLE, KEY_DESCRIPTION, KEY_CONTENT, KEY_COMMENTS, KEY_AUTHOR, KEY_DATE, KEY_ID }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		Article article = null;

		try {
			if (cursor != null)
				cursor.moveToFirst();

			article = new Article(Arrays.asList(cursor.getString(0).split("_PCX_")), Uri.parse(cursor.getString(1)), Uri.parse(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getLong(8), cursor.getInt(9));

		}
		finally {
			cursor.close();
		}

		db.close();
		return article;
	}

	public List<Article> getAll() {
		List<Article> articleList = new ArrayList<Article>();
		String selectQuery = "SELECT  * FROM " + TABLE_ARTICLES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToLast()) {
			do {
				articleList.add(new Article(Arrays.asList(cursor.getString(0).split("_PCX_")), Uri.parse(cursor.getString(1)), Uri.parse(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getLong(8), cursor.getInt(9)));
			} while (cursor.moveToPrevious());
		}
		db.close();

		return articleList;
	}

	public boolean contains(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_ARTICLES, new String[] { KEY_TAGS, KEY_SOURCE, KEY_IMAGE, KEY_TITLE, KEY_DESCRIPTION, KEY_CONTENT, KEY_COMMENTS, KEY_AUTHOR, KEY_DATE, KEY_ID }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

		boolean exists = cursor.moveToFirst();
		db.close();
		return exists;
	}

	public void delete(Article article) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ARTICLES, KEY_ID + " = ?", new String[] { String.valueOf(article.getId()) });
		db.close();
	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ARTICLES, null, null);
		db.close();
	}

	public int getCount() {
		int count = 0;
		String countQuery = "SELECT  * FROM " + TABLE_ARTICLES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		if (cursor != null && !cursor.isClosed()) {
			count = cursor.getCount();
			cursor.close();
		}

		db.close();
		return count;
	}
}