package com.ravi.bakingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public PreferencesUtils(Context context){
        String main_key = JsonKeys.CACHE_MAIN_KEY;
        this.sharedPref = context.getSharedPreferences(main_key, Context.MODE_PRIVATE);
    }

    public void setData(String key, String value){
        editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getData(String key){
        return sharedPref.getString(key, "");
    }
}
