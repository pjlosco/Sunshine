package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;
    private String mLocation;
    private static final int DETAIL_LOADER = 0;
    public static final String DATE_KEY = "forecast_date";
    public static final String LOCATION_KEY = "location";

    private TextView mLocationView;
    private ImageView mIconView;
    private TextView mDateView;
    private TextView mDayView;
    private TextView mForecastView;
    private TextView mHighView;
    private TextView mLowView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        Bundle args = getArguments();
        if (args != null && args.containsKey(DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LOCATION_KEY, mLocation);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null && args.containsKey(DATE_KEY) &&
                mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mLocationView = (TextView) rootView.findViewById(R.id.location_name);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon_imageview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mDayView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mForecastView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //always returns to Sunshine app instead of share app when brought back from background
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share action provider is null for unknown reason");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] forecastColumns = {
                WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
                WeatherEntry.COLUMN_DATETEXT,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_MIN_TEMP,
                WeatherEntry.COLUMN_HUMIDITY,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_WIND_SPEED,
                WeatherEntry.COLUMN_DEGREES,
                WeatherEntry.COLUMN_WEATHER_ID,
                LocationEntry.COLUMN_LOCATION_CODE
        };

        String forecastDate = getArguments().getString(DATE_KEY);

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());

        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithDateUri(mLocation, forecastDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                forecastColumns,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }
        boolean isMetric = Utility.isMetric(getActivity());
        int weatherId = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));
        String weatherDescription = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
        String dateString = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_DATETEXT));
        String dateText = Utility.getFormattedMonthDay(getActivity(), dateString);
        String dayText = Utility.getFriendlyDayString(getActivity(), dateString);
        if (dayText.contains("Today")) {
            dayText = "Today";
        }
        String high = Utility.formatTemperature(getActivity(), data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
        String low = Utility.formatTemperature(getActivity(), data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP)), isMetric);

        String humidity = Utility.formatHumidity(getActivity(), data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY)));
        String wind = Utility.getFormattedWind(getActivity(),
                data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED)),
                data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES)));
        String pressure = Utility.formatPressure(getActivity(), data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_PRESSURE)));

        String locationName = data.getString(data.getColumnIndex(LocationEntry.COLUMN_LOCATION_CODE));

        mLocationView.setText(locationName);
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        mIconView.setContentDescription(weatherDescription);
        mDateView.setText(dateText);
        mDayView.setText(dayText);
        mForecastView.setText(weatherDescription);
        mHighView.setText(high);
        mLowView.setText(low);
        mHumidityView.setText(humidity);
        mWindView.setText(wind);
        mPressureView.setText(pressure);

        // for sharing URL
        mForecastStr = String.format("%s - %s - %s/%s",
                dateString,
                weatherDescription,
                high, low);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
}
