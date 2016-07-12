package com.ashojash.android.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import com.ashojash.android.util.EventProvider;
import com.ashojash.android.util.ObscuredSharedPrefs;
import com.ashojash.android.util.UiUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

public class AppController extends MultiDexApplication {
    public static final String TAG = "ashojash";
    public static final int REFRESH_TOKEN_REQUEST_CODE = 1000001;
    public static Context context;
    public static Activity currentActivity;
    public static LayoutInflater layoutInflater;
    private static AppController instance;
    public static final Handler HANDLER = new Handler();
    public static SharedPreferences defaultPref;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences obsPref;
    public static SharedPreferences.Editor obsEditor;
    public static final Gson gson = new Gson();
    public static int widthPx;
    public static int heightPx;
    public static int widthDp;
    public static int heightDp;
    private Tracker mTracker;
    public static final int ANDROID_VERSION = Build.VERSION.SDK_INT;
    public static String citySlug;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        instance = this;
        initializeSharedPrefs();
        initializePhoneDimes();
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());
        citySlug = AppController.defaultPref.getString("current_city_slug", null);
    }

    private void initializePhoneDimes() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            widthPx = size.x;
            heightPx = size.y;
        } else {
            widthPx = display.getWidth();
            heightPx = display.getHeight();
        }
        widthDp = (int) UiUtil.px2dp(widthPx);
        heightDp = (int) UiUtil.px2dp(heightPx);
    }

    private void initializeSharedPrefs() {
        defaultPref = this.getSharedPreferences("com.ashojash.android.ashojash_prefs", Context.MODE_PRIVATE);
        obsPref = new ObscuredSharedPrefs(AppController.context, AppController.context.getSharedPreferences("com.ashojash.android.ashojash_auth_prefs", Context.MODE_PRIVATE));
        editor = defaultPref.edit();
        obsEditor = obsPref.edit();
    }

    public static synchronized AppController getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // install multidex
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            analytics.setLocalDispatchPeriod(1800);
            mTracker = analytics.newTracker("UA-70125480-1");
            mTracker.enableExceptionReporting(true);
            mTracker.enableAdvertisingIdCollection(true);
            mTracker.enableAutoActivityTracking(true);
        }
        return mTracker;
    }

    public static EventProvider eventProvider = new EventProvider();
}
