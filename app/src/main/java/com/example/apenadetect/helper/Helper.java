package com.example.apenadetect.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.apenadetect.R;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Properties;

public final class Helper {
    public static String GetConfigValue(Context context, String name){
        Resources resources = context.getResources();

        try{
            InputStream is = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(is);
            return properties.getProperty(name);
        }
        catch (Resources.NotFoundException e){
            Log.e("GET PROPERTIES ERROR", "Unable to find the config file: " + e.getMessage());
        }
        catch (IOException e){
            Log.e("GET PROPERTIES ERROR", "Failed to open config file.");
        }

        return null;
    }
    public static <T> T ConvertJson(String json, Class<T> cls){
        Gson gson = new Gson();
//        Type type = TypeToken.getParameterized(cls, cls).getType();
        return gson.fromJson(json,cls);
    }
}
