package com.trienoir.ultimatetimesheetmaker.utilities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.trienoir.ultimatetimesheetmaker.R;
import com.trienoir.ultimatetimesheetmaker.activities.MainActivity;
import com.trienoir.ultimatetimesheetmaker.enums.TimeFormat;

/**
 * Created by TrieNoir on 17/02/2016.
 */
public class WidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_TIME_IN = "ActionReceiverTimeIn";
    public static String ACTION_WIDGET_OPEN = "ActionReceiverOpen";
    public static String ACTION_WIDGET_HOLIDAY= "ActionReceiverHoliday";
    public static String ACTION_WIDGET_TIME_OUT = "ActionReceiverTimeOut";
    Intent intent;
    PendingIntent pendingIntent;



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int widgetId : appWidgetIds) {
            DatabaseCommands.InitDao(context);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            setActionOnClick(context, widgetId, remoteViews, R.id.widget_layout_time_in, ACTION_WIDGET_TIME_IN);
            setActionOnClick(context, widgetId, remoteViews, R.id.widget_layout_open, ACTION_WIDGET_OPEN);
            setActionOnClick(context, widgetId, remoteViews, R.id.widget_layout_holiday, ACTION_WIDGET_HOLIDAY);
            setActionOnClick(context, widgetId, remoteViews, R.id.widget_layout_time_out, ACTION_WIDGET_TIME_OUT);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_TIME_IN)) { context.startActivity(setIntentValue(context, 0)); }
        else if (intent.getAction().equals(ACTION_WIDGET_OPEN)) { context.startActivity(setIntentValue(context, 1)); }
        else if (intent.getAction().equals(ACTION_WIDGET_HOLIDAY)) { context.startActivity(setIntentValue(context, 2)); }
        else if (intent.getAction().equals(ACTION_WIDGET_TIME_OUT)) { context.startActivity(setIntentValue(context, 3)); }
        else { super.onReceive(context, intent); }
    }

    private void setActionOnClick(Context context, int widgetId, RemoteViews remoteViews, int resourceId, String action) {
        intent = new Intent(context, WidgetProvider.class);
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(resourceId, pendingIntent);
    }

    private Intent setIntentValue(Context context, int value) {
        Intent intent = new Intent(context, MainActivity.class);
        switch (value) {
            case 0: intent.putExtra("timeIn", ""); break;
            case 1: intent.putExtra("open", ""); break;
            case 2: intent.putExtra("holiday", ""); break;
            case 3: intent.putExtra("timeOut", ""); break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
