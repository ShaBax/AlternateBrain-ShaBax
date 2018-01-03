package innovationsquare.com.alternatebrain.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {
    public static final String API_KEY = "api_key";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    public static final String USER_NAME = "username";
    public static final String SECTOR = "sector";
    public static final String USER_ID = "user_id";
    public static final String REFERRAL = "referral";
    public static final String EMAIL = "email";
    public static final String UP_IMAGE = "imgg";
    public static final String BACK_FILE_NAME = "filenamebackground";
    public static final String MY_LATITUDE = "my_lat";
    public static final String MY_LONGITUDE = "my_long";
    public static final String MY_LATITUDE_FOR_UPLOAD = "my_lat_upload";
    public static final String MY_LONGITUDE_FORUPLOAD = "my_long_upload";


    public static void putString(Context ctx, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(key, "");
    }

    public static void putBool(Context ctx, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    public static boolean getBool(Context ctx, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean(key, false);
    }

    public static void clearPrefernce(Context ctx){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
