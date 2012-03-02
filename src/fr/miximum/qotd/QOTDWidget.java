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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class QOTDWidget extends AppWidgetProvider {
    public static final String ACTION_SHOW_QUOTE = "fr.miximum.qotd.SHOW_QUOTE";
    public static final String ACTION_CHANGE_QUOTE = "fr.miximum.qotd.CHANGE_QUOTE";

    @Override
    public void onReceive(Context context, Intent intent) {

        // When we tap on the widget
        if (ACTION_SHOW_QUOTE.equals(intent.getAction())) {
            Log.d("QOTD", "Widget : show quote");
            Intent show = new Intent(context, QOTD.class);
            show.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(show);
        }

        // New quote requested
        else if (ACTION_CHANGE_QUOTE.equals(intent.getAction())) {
            Log.d("QOTD", "Widget : change quote");
            Intent update = new Intent(context, UpdateService.class);
            update.setAction(intent.getAction());
            context.startService(update);
        }

        // Updating widget
        else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            Log.d("QOTD", "Widget : update");
            Intent update = new Intent(context, UpdateService.class);
            update.setAction(intent.getAction());
            context.startService(update);
        }

        super.onReceive(context, intent);
    }

    /** Service dedicated to quotation updating */
    public static class UpdateService extends Service {

        private QuoteProvider mQuoteProvider;

        @Override
        public int onStartCommand(Intent intent, int startId, int flag) {
            Log.d("QOTD", "Starting update service");
            mQuoteProvider = new QuoteProvider(this);

            // if a new quote is required
            if (ACTION_CHANGE_QUOTE.equals(intent.getAction())) {
                mQuoteProvider.resetQuote();
            }

            // Rebuilding widget
            RemoteViews views = buildUpdate(this);
            ComponentName widget = new ComponentName(this, QOTDWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget, views);

            // Setting up alarm
            startAlarm(this);

            return START_NOT_STICKY;
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
            String currentQuote = mQuoteProvider.getCurrentQuote();
            views.setTextViewText(R.id.qotd_widget_text, currentQuote);

            return views;
        }

        /** Configure alarm to update quote */
        private void startAlarm(Context context) {

            // Get update frequency
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            int frequency = Integer.parseInt(sp.getString("quote_update_frequency", "8640000"));
            Log.d("QOTD", "Setting next update in : " + frequency);

            // Update quote intent
            Intent intent = new Intent(context, QOTDWidget.class);
            intent.setAction(ACTION_CHANGE_QUOTE);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // Set up alarm
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
            am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + frequency, pi);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
