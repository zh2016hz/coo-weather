package com.example.administrator.coolweather.util;

import javax.security.auth.callback.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by aa on 17/3/28.
 * 网络请求封装类
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String addres, okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request build = builder.url(addres).build();
        okHttpClient.newCall(build).enqueue(callback);
    }
}
