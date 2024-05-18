package com.example.apenadetect;

import android.content.Context;

import com.example.apenadetect.helper.Helper;

public class ApenaApplication {
    public static String WS_HOST;
    public static int WS_PORT;
    public ApenaApplication(Context context){
        WS_HOST = Helper.GetConfigValue(context, "ws_host");
        WS_PORT = Integer.parseInt(Helper.GetConfigValue(context,"ws_port"));
    }

}
