package com.example.android.sunshine.app.test;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.app.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.app.data.WeatherDBHelper;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testDeleteDB() throws Throwable {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

    public void testInsertReadProvider() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.

        ContentValues locationValues = TestDB.createNorthPoleLocationValues();


        Uri insertUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
        long locationRowId = ContentUris.parseId(insertUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDB.validateCursor(cursor, locationValues);

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDB.validateCursor(cursor, locationValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestDB.createWeatherValues(locationRowId);

        insertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
        long weatherRowId = ContentUris.parseId(insertUri);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDB.validateCursor(weatherCursor, weatherValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(weatherValues, locationValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationUri(TestDB.TEST_LOCATION),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDB.validateCursor(weatherCursor, weatherValues);

        // Get the joined Weather and Location data with a start date
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDateUri(
                        TestDB.TEST_LOCATION, TestDB.TEST_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDB.validateCursor(weatherCursor, weatherValues);

        // Get the joined Weather and Location data with a specific date
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDateUri(
                        TestDB.TEST_LOCATION, TestDB.TEST_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDB.validateCursor(weatherCursor, weatherValues);
    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationUri(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDateUri(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }
}
