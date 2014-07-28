package com.example.android.sunshine.app.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.app.data.WeatherContract.*;
import com.example.android.sunshine.app.data.WeatherDBHelper;

public class TestDB extends AndroidTestCase {

    public static final String LOG_TAG = TestDB.class.getSimpleName();

    public void testCreateDB() throws Throwable {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDBHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDB() {
        String testLocationName = "North Pole";
        String testPostalCode = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        WeatherDBHelper dbHelper = new WeatherDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_LOCATION_NAME, testLocationName);
        values.put(LocationEntry.COLUMN_POSTAL_CODE, testPostalCode);
        values.put(LocationEntry.COLUMN_COORD_LATITUDE, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONGITUDE, testLongitude);

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Specify which columns you want.
        String[] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_POSTAL_CODE,
                LocationEntry.COLUMN_LOCATION_NAME,
                LocationEntry.COLUMN_COORD_LATITUDE,
                LocationEntry.COLUMN_COORD_LONGITUDE
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_POSTAL_CODE);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_LOCATION_NAME));
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LATITUDE));
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LONGITUDE));
            double longitude = cursor.getDouble(longIndex);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(testLocationName, name);
            assertEquals(testPostalCode, location);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);

            // Fantastic.  Now that we have a location, add some weather!
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

            long weatherRowID = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowID != -1);

            Cursor weatherCursor = db.query(
                    WeatherEntry.TABLE_NAME,
                    null,
                    null, // Columns for the "where" clause
                    null, // Values for the "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null // sort order
            );

            if (weatherCursor.moveToFirst()) {
                int dateIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_DATETEXT));
                String date = weatherCursor.getString(dateIndex);

                int degreesIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_DEGREES));
                double degrees = weatherCursor.getDouble(degreesIndex);

                int humidityIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_HUMIDITY));
                double humidity = weatherCursor.getDouble(humidityIndex);

                int pressureIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_PRESSURE));
                double pressure = weatherCursor.getDouble(pressureIndex);

                int maxIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_MAX_TEMP));
                double maxTemp= weatherCursor.getDouble(maxIndex);

                int minIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_MIN_TEMP));
                double minTemp = weatherCursor.getDouble(minIndex);

                int descriptionIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_SHORT_DESC));
                double shortDescription = weatherCursor.getDouble(descriptionIndex);

                int windIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_WIND_SPEED));
                double windSpeed = weatherCursor.getDouble(windIndex);

                int weatherIndex = weatherCursor.getColumnIndex((WeatherEntry.COLUMN_WEATHER_ID));
                int weather_id = weatherCursor.getInt(weatherIndex);
            } else {
                fail("No weather data returned!");
            }

            dbHelper.close();
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
    }
}
