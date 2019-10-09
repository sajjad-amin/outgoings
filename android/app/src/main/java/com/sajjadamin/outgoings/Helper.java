package com.sajjadamin.outgoings;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    private Context context;

    Helper(Context context) {
        this.context = context;
    }

    String getSessionData(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("session_data", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    boolean sessionCheck() {
        return !getSessionData("user_id").equals("") || !getSessionData("firstname").equals("")
                || !getSessionData("lastname").equals("") || !getSessionData("email").equals("")
                || !getSessionData("phone").equals("") || !getSessionData("reg_date").equals("")
                || !getSessionData("password").equals("");
    }
    public boolean connectionCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
    }
    public int autoAddition(String input){
        int output = 0;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);
        while(matcher.find()){
            output += Integer.parseInt(matcher.group());
        }
        return output;
    }
}
