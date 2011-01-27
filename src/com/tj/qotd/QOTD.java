package com.tj.qotd;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class QOTD extends Activity {

    private QuoteProvider mQuoteProvider;

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
            startActivityForResult(new Intent(this, QOTDPreferences.class), 1);
            break;
        }

        return true;
    }

    /** Requires a new quote, updates ui and widget */
    public void updateQuote() {
        mQuoteProvider.resetQuote();
        displayCurrentQuote();

        // Send update message via broadcast intent
        Context context = getApplicationContext();
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, QOTDWidget.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        Intent update = new Intent();
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(update);
    }
}
