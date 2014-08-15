package com.example.android.sunshine.app.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by patricklosco on 8/13/14.
 */
public class SunshineService extends IntentService {
    public SunshineService() {
        super("SunshineService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
