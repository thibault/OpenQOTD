package com.tj.qotd;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuoteProvider {

    private static final String DATABASE_NAME = "qotd.db";
    private static final int DATABASE_VERSION = 1;
    private static final String QUOTES_TABLE_NAME = "quotes";

    private static final String[] projection = new String[] {
        Quote._ID,
        Quote.QUOTE,
        Quote.AUTHOR
    };

    /** Helps create and open the database file */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context mContext;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("QOTD", "db helper onCreate");
            db.execSQL("CREATE TABLE " + QUOTES_TABLE_NAME + " ("
                + Quote._ID + " INTEGER PRIMARY KEY,"
                + Quote.QUOTE + " TEXT,"
                + Quote.AUTHOR + " TEXT"
                + ");" );

            insertFixtures(db);
        }

        /** insert default data in db */
        public void insertFixtures(SQLiteDatabase db) {
            Log.d("QOTD", "Insert fixtures");
            Resources res = mContext.getResources();
            String[] quotes = res.getStringArray(R.array.quote_fixtures);
            for (String s : quotes) {
                String[] splitted = s.split("\\|");

                ContentValues cv = new ContentValues();
                cv.put(Quote.QUOTE, splitted[0]);
                cv.put(Quote.AUTHOR, splitted[1]);

                db.insert(QUOTES_TABLE_NAME, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("QOTD", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + QUOTES_TABLE_NAME);
            onCreate(db);
        }
    }
    private DatabaseHelper mDbHelper;
    private Context mContext;

    public QuoteProvider(Context context) {
        mDbHelper = new DatabaseHelper(context);
        mContext = context;
    }

    /**    Generate a new quote */
    public String resetQuote() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.query(QUOTES_TABLE_NAME, projection, null, null, null, null, "RANDOM()", "1");

        if (c.moveToFirst()) {
            int currentQuoteId = c.getInt(0);
            SharedPreferences sp = mContext.getSharedPreferences("qotd", Activity.MODE_PRIVATE);
            Editor editor = sp.edit();
            editor.putInt("qotd.current_quote_id", currentQuoteId);
            editor.commit();
        }

        return getQuoteFromCursor(c);
    }

    public String getCurrentQuote() {
        SharedPreferences sp = mContext.getSharedPreferences("qotd", Activity.MODE_PRIVATE);
        int currentQuoteId = sp.getInt("qotd.current_quote_id", -1);

        if (currentQuoteId < 0) {
            return resetQuote();
        }
        else
        {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = db.query(QUOTES_TABLE_NAME, projection, Quote._ID + "=" + currentQuoteId, null, null, null, "RANDOM()", "1");
            return getQuoteFromCursor(c);
        }
    }

    /** Take a cursor, and return a formatted quote */
    private String getQuoteFromCursor(Cursor c) {
        String res = "";
        if (c.moveToFirst()) {
            res = c.getString(1).concat(" — ").concat(c.getString(2));
        }
        c.close();
        return res;
    }
}
