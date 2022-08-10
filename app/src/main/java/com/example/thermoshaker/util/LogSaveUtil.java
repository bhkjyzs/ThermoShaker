package com.example.thermoshaker.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.thermoshaker.R;


public class LogSaveUtil {
    private static final String LOCALE_LOGO = "LOCALE_LOGO";
    private static final String LOCALE_LOGO_KEY = "LOCALE_LOGO_KEY";

    private static final String LOCALE_NAME = "LOCALE_NAME";
    private static final String LOCALE_NAME_KEY = "LOCALE_NAME_KEY";


    public static int getLogo(Context context) {
        SharedPreferences spLocale = context.getSharedPreferences(LOCALE_LOGO, Context.MODE_PRIVATE);
        String localeJson = spLocale.getString(LOCALE_LOGO_KEY, String.valueOf(R.drawable.logo));
        return Integer.parseInt(localeJson);
    }


    public static void setLogo(Context pContext, int ResID) {
        SharedPreferences spLocal = pContext.getSharedPreferences(LOCALE_LOGO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = spLocal.edit();
        edit.putString(LOCALE_LOGO_KEY, String.valueOf(ResID));
        edit.apply();
    }

    public static String getName(Context context) {
        SharedPreferences spLocale = context.getSharedPreferences(LOCALE_NAME, Context.MODE_PRIVATE);
        String localeJson = spLocale.getString(LOCALE_NAME_KEY, "");
        return localeJson;
    }


    public static void setName(Context pContext, String ResID) {
        SharedPreferences spLocal = pContext.getSharedPreferences(LOCALE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = spLocal.edit();;
        edit.putString(LOCALE_NAME_KEY, ResID);
        edit.apply();
    }
}

