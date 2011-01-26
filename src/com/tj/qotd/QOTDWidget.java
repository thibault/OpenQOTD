package com.tj.qotd;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class QOTDWidget extends AppWidgetProvider {
    public static final String ACTION_SHOW_QUOTE = "com.tj.qotd.SHOW_QUOTE";

    public static final int MAX_QUOTE_LEN_IN_WIDGET = 130;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("QOTD", "Widget onUpdate");
        context.startService(new Intent(context, UpdateService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("QOTD", "Widget onReceive action : " + intent.getAction());

        if (intent.getAction().equals(ACTION_SHOW_QUOTE)) {
            Log.d("QOTD", "In show quote action");
            Intent show = new Intent(context, QOTD.class);
            show.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(show);
        }

        super.onReceive(context, intent);
    }

    /** Service dedicated to quotation updating */
    public static class UpdateService extends Service {

        @Override
        public void onStart(Intent intent, int startId) {
            Log.d("QOTD", "Starting update service");

            RemoteViews views = buildUpdate(this);

            ComponentName widget = new ComponentName(this, QOTDWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget, views);
        }

        /** Build the ui update */
        public RemoteViews buildUpdate(Context context) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.qotd_widget_layout);

            // Intent to show full quote
            Intent bcast = new Intent(context, QOTDWidget.class);
            bcast.setAction(ACTION_SHOW_QUOTE);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, bcast, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.qotd_layout, pending);

            // Update quote
            QuoteProvider quoteProvider = new QuoteProvider(context);
            String currentQuote = quoteProvider.getCurrentQuote();
            if (currentQuote.length() > MAX_QUOTE_LEN_IN_WIDGET) {
                currentQuote = currentQuote.substring(0, MAX_QUOTE_LEN_IN_WIDGET) + "…";
            }
            views.setTextViewText(R.id.qotd_widget_text, currentQuote);

            return views;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
