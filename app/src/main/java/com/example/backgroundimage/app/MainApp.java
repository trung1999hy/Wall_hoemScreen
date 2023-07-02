package com.example.backgroundimage.app;

import android.app.Application;

import com.example.backgroundimage.utils.Preference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainApp extends Application {

    private static Preference preference = null;
    private static MainApp application = null;
    public static Preference getInstance() {
        if (preference == null) {
            preference = Preference.buildInstance(application);
        }
        preference.isOpenFirst();
        return preference;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
