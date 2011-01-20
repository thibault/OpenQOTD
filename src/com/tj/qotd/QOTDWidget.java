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

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d("QOTD", "Widget onUpdate");
		context.startService(new Intent(context, UpdateService.class));
	}

	public static class UpdateService extends Service {

		@Override
		public void onStart(Intent intent, int startId) {
			Log.d("QOTD", "Starting update service");

			RemoteViews views = buildUpdate(this);

			ComponentName widget = new ComponentName(this, QOTDWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget, views);
		}

        public RemoteViews buildUpdate(Context context) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.qotd_widget_layout);

            // Update intent
            Intent bcast = new Intent(context, QOTDWidget.class);
            bcast.setAction(ACTION_CHANGE_QUOTATION);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, bcast, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.layout.qotd_widget_layout, pending);

            // Update quote
        	QuoteProvider quoteProvider = new QuoteProvider();
            views.setTextViewText(R.id.qotd_widget_text, quoteProvider.getQuote());

            return views;
        }

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
	}
}