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
    public static final String ACTION_CHANGE_QUOTATION = "com.tj.qotd.CHANGE_QUOTATION";
    public static final String ACTION_SHOW_QUOTE = "com.tj.qotd.SHOW_QUOTE";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d("QOTD", "Widget onUpdate");
		context.startService(new Intent(context, UpdateService.class));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        Log.d("QOTD", "Widget onReceive action : " + intent.getAction());

        // User requires a new quote
        if (intent.getAction().equals(ACTION_CHANGE_QUOTATION)) {
        	Log.d("QOTD", "In change quotation action");
        	Intent update = new Intent(context, UpdateService.class);
        	update.setAction(intent.getAction());
            context.startService(update);
            
        // User wants to show current quote
        } else if (intent.getAction().equals(ACTION_SHOW_QUOTE)) {
            Log.d("QOTD", "In show quotation action");
            QuoteProvider quoteProvider = new QuoteProvider();
            Intent show = new Intent(context, QOTD.class);
            show.putExtra("quote", quoteProvider.getCurrentQuote());
            Log.d("QOTD", quoteProvider.getCurrentQuote());
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

            // Intent to change quote
            Intent bcast = new Intent(context, QOTDWidget.class);
            bcast.setAction(ACTION_CHANGE_QUOTATION);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, bcast, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.qotd_widget_icon, pending);

            // Intent to show full quote
            bcast = new Intent(context, QOTDWidget.class);
            bcast.setAction(ACTION_SHOW_QUOTE);
            pending = PendingIntent.getBroadcast(context, 0, bcast, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.qotd_widget_text, pending);

            // Update quote
        	QuoteProvider quoteProvider = new QuoteProvider();
            views.setTextViewText(R.id.qotd_widget_text, quoteProvider.getRandomQuote());

            return views;
        }

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
	}
}
