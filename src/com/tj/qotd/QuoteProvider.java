package com.tj.qotd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Single entry point to get some quotes
 *
 * We don't extend ContentProvider because we don't need to provide quotes to other apps
 */
public class QuoteProvider {

    /**
     * The basic db name, located is the asset directory
     */
    private static final String DB_NAME = "qotd.db";

    /**
     * We need to suffix the db with a dummy extension, to prevent android to compress it
     */
    private static final String DB_FILE_FAKE_EXTEN = ".mp3";

    /**
     * Current db version
     */
    private static final int DB_VERSION = 1;

    /**
     * Name of the table containing quotes
     */
    private static final String QUOTES_TABLE_NAME = "quote";

    /**
     * Helper to manage database creation and upgrade
     */
    private DatabaseHelper mDbHelper;

    private Context mContext;

    /**
     * Fields to fetch when requesting quotes
     */
    private static final String[] projection = new String[] {
        Quote._ID,
        Quote.QUOTE
    };

    /** Helps create and open the database file */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private Context mContext;
        private boolean createDatabase = false;
        private boolean upgradeDatabase = false;

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        /** Db will be created later */
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("QOTD", "Database needs to be created");
            createDatabase = true;
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("QOTD", "Database needs to be upgraded");
            upgradeDatabase = true;
        }

        /** This method has to be called after constructor */
        public void initializeDatabase() {
            Log.d("QOTD", "Initializing db");

            // Make sure we request database, to initialize creation variables
            getWritableDatabase();

            if (createDatabase) {
                copyDatabase();
            }
            else if (upgradeDatabase) {
                upgradeDatabase();
            }
        }

        /* To create database, we copy the one provided in assets */
        public void copyDatabase() {
            Log.d("QOTD", "Creating database");

            // We need to close the db handle to overwrite the newly created db
            close();

            try {
                // Fixtures db is in assets
                InputStream myInput = mContext.getAssets().open(DB_NAME + DB_FILE_FAKE_EXTEN);

                //Open the empty db as the output stream
                String outFileName = mContext.getDatabasePath(DB_NAME).getAbsolutePath();
                OutputStream myOutput = new FileOutputStream(outFileName);

                //transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();

                // Access the db to set the access timestamp (prevents a "corrupted" error)
                getWritableDatabase();
            } catch (IOException ioe) {
                Log.e("QOTD", "Cannot create db : " + ioe.getMessage());
            }
        }

        /** Just wipe out the current db, and replace it with the fixture's one */
        public void upgradeDatabase() {
            Log.d("QOTD", "Updating database.");
            copyDatabase();
        }
    }

    public QuoteProvider(Context context) {
        mContext = context;
        mDbHelper = new DatabaseHelper(context);
        mDbHelper.initializeDatabase();
    }

    /**
     * Fetch a randow quote, and save its id for later access
     */
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

    /**
     * Fetch a previously generated quote
     */
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

    /**
     * Take a cursor, and return a formatted quote
     */
    private String getQuoteFromCursor(Cursor c) {
        String res = "";
        if (c.moveToFirst()) {
            res = c.getString(1);
        }
        c.close();
        return res;
    }
}
