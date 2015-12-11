package com.ashojash.android.helper;

import android.content.res.Configuration;

import java.util.Locale;

public class LangHelper {
    public static void setLang(String localeStr) {
        Locale locale = new Locale(localeStr);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        AppController.context.getResources().updateConfiguration(config, AppController.context.getResources().getDisplayMetrics());
        /*Locale locale = new Locale("fa_IR");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/
    }

}
