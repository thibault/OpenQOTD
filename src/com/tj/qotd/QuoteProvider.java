package com.tj.qotd;

import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class QuoteProvider {

    private static final String DATABASE_NAME = "qotd.db";
    private static final int DATABASE_VERSION = 1;
    private static final String QUOTES_TABLE_NAME = "quotes";

    /** Helps create and open the database file */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + QUOTES_TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY,"
                + "quote TEXT"
                + ");" );
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	
        }
    }
    private DatabaseHelper mDbHelper;

	public static String[] quotations = new String[] {
		"First quotation — Me",
		"Second quotation — Me",
		"Another quotation — Me",
		"This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines — Still Me"
	};
	public static int quoteId;

    /*public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }*/

	/** Change the current quote */
	public String resetQuote() {
		int nbStrings = quotations.length;
		Random rand = new Random();
        int stringIndex = rand.nextInt(nbStrings);

        quoteId = stringIndex;
        return quotations[stringIndex];
	}
	
	/**	Get the actual quote */
	public String getCurrentQuote() {
		return quotations[quoteId];
	}
}
