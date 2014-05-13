package com.stylingandroid.adapters.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CounterHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "counter.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "counter";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TEXT = "text";
	
	public static final String[] NUMBERS = {
		"One", "Two", "Three", "Four", "Five",
		"Six", "Seven", "Eight", "Nine", "Ten",
		"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
		"Sixteen", "Seventeen", "Eighteen", "Nineteen", "Tewnty"
	};
	
	public static final long MAX_ROWS = NUMBERS.length;
	
	public CounterHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("create table " + TABLE_NAME + " (" + 
	        COLUMN_ID + " integer primary key autoincrement, " + 
		    COLUMN_TEXT + " text not null);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	    onCreate(db);
	}

}
