package com.example.android.appwidgetsample;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    //you'll use shared preferences to keep track of the current update for the widget.
    private static final String mSharedPrefFile =
            "com.example.android.appwidgetsample";
    private static final String COUNT_KEY = "count";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // get the value of the update count from the shared preferences, and increment that value.
        // Each app widget may have a different update count so each app widget needs its own entry in the shared preferences.
        SharedPreferences prefs = context.getSharedPreferences(
                mSharedPrefFile, 0);
        int count = prefs.getInt(COUNT_KEY + appWidgetId, 0);
        count++;

        // Get the current time.
        String dateString =
                DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

        // Construct the RemoteViews object.
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_id,
                String.valueOf(appWidgetId));
        views.setTextViewText(R.id.appwidget_update,
                context.getResources().getString(
                        R.string.date_count_format, count, dateString));

        // Save count back to prefs.
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putInt(COUNT_KEY + appWidgetId, count);
        prefEditor.apply();

        // Setup update button to send an update request as a pending intent.
        Intent intentUpdate = new Intent(context, NewAppWidget.class);

        // The intent action must be an app widget update.
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Include the widget ID to be updated as an intent extra.
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        // Wrap it all in a pending intent to send a broadcast.
        // Use the app widget ID as the request code (third argument) so that
        // each intent is unique.
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context,
                appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // Assign the pending intent to the button onClick handler
        views.setOnClickPendingIntent(R.id.button_update, pendingUpdate);

        // Instruct the widget manager to update the widget.
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    //. This method is called the first time the widget runs and again each time the widget receives an update request (a broadcast intent).
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

}