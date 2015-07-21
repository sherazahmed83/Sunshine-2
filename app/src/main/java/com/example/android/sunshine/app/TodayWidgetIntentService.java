package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by Sheraz on 7/20/2015.
 */
public class TodayWidgetIntentService extends IntentService {

    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                        TodayWidgetProvider.class));

        String locationSetting = Utility.getPreferredLocation(this);

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cursor = getContentResolver().query(weatherForLocationUri, ForecastFragment.FORECAST_COLUMNS, null, null, sortOrder);

        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        if(cursor != null && cursor.getCount() > 0) {
            double highTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
            double minTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
            String weatherDescription = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
            int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
            int weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
            String description = Utility.getStringForWeatherCondition(this, weatherId);
            cursor.close();

            String formattedMaxTemperature = Utility.formatTemperature(this, highTemp);
            String formattedMinTemperature = Utility.formatTemperature(this, minTemp);

            // Perform this loop procedure for each Today widget
            for (int appWidgetId : appWidgetIds) {
                int layoutId;
                int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
                int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
                int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);

                if (widgetWidth >= largeWidth) {
                    layoutId = R.layout.widget_today_large;
                } else if (widgetWidth >= defaultWidth) {
                    layoutId = R.layout.widget_today;
                } else {
                    layoutId = R.layout.widget_today_small;
                }

                RemoteViews views = new RemoteViews(getPackageName(), layoutId);

                // Add the data to the RemoteViews
                views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
                // Content Descriptions for RemoteViews were only added in ICS MR1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }
                views.setTextViewText(R.id.widget_description, weatherDescription);
                views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
                views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);
                // Create an Intent to launch MainActivity
                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp, displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
    }
}
