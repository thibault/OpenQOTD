/*
 * © Copyright 2011 Thibault Jouannic <thibault@jouannic.fr>. All Rights Reserved.
 *  This file is part of OpenQOTD.
 *
 *  OpenQOTD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  OpenQOTD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenQOTD. If not, see <http://www.gnu.org/licenses/>.
 */

package fr.miximum.qotd;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class QOTD extends Activity {

    private QuoteProvider mQuoteProvider;

    private static int REQUEST_PREFERENCES;

    private static String APPS_URL = "http://apps.miximum.fr/en/";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mQuoteProvider = new QuoteProvider(getApplicationContext());
    }

    /** Each time we show the activity, we display the current quote */
    @Override
    public void onStart()
    {
        displayCurrentQuote();
        super.onStart();
    }

    /** Show the current quote in the activity */
    public void displayCurrentQuote() {
        TextView text = (TextView) findViewById(R.id.activity_quote_text);
        text.setText(mQuoteProvider.getCurrentQuote());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_quote_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.update_quote:
            updateQuote();
            break;
        case R.id.settings:
            startActivityForResult(new Intent(this, QOTDPreferences.class), REQUEST_PREFERENCES);
            break;
        case R.id.about:
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(APPS_URL));
            startActivity(browserIntent);
            break;
        }

        return true;
    }

    /** Update quotes if preferences changed */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == QOTDPreferences.RESULT_UPDATED) {
            updateQuote();
        }
    }

    /** Requires a new quote, updates ui and widget */
    public void updateQuote() {
        mQuoteProvider.resetQuote();
        displayCurrentQuote();

        // Send update message via broadcast intent
        Context context = getApplicationContext();
        Intent update = new Intent(context, QOTDWidget.class);
        update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(update);
    }
}
