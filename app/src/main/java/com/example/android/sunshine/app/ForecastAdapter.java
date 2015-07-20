package com.example.android.sunshine.app;

/**
 * Created by Sheraz on 6/14/2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    private boolean mUseTodayLayout;

    public void setUseTodayLayout(boolean mUseTodayLayout) {
        this.mUseTodayLayout = mUseTodayLayout;
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;

        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return ( position == 0 && mUseTodayLayout ) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String date     = Utility.getFriendlyDayString(context, cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        double highTemp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double lowTemp  = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);

        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        String desc     = Utility.getStringForWeatherCondition(context, weatherId);
        int viewType = getItemViewType(cursor.getPosition());
        int fallbackIconId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            fallbackIconId = Utility.getArtResourceForWeatherCondition(weatherId);

        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            fallbackIconId = Utility.getIconResourceForWeatherCondition(weatherId);
        }

        Glide.with(mContext)
                .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                .error(fallbackIconId)
                .crossFade()
                .into(viewHolder.iconView);

        viewHolder.dateView.setText(date);

        viewHolder.descriptionView.setText(desc);

        // For accessibility, add a content description to the icon field
        viewHolder.descriptionView.setContentDescription(context.getString(R.string.a11y_forecast, desc));

        viewHolder.highTempView.setText(Utility.formatTemperature(mContext, highTemp));
        viewHolder.highTempView.setContentDescription(context.getString(R.string.a11y_high_temp, highTemp));

        viewHolder.lowTempView.setText(Utility.formatTemperature(mContext, lowTemp));
        viewHolder.lowTempView.setContentDescription(context.getString(R.string.a11y_low_temp, lowTemp));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}