package com.example.administrator.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;

import com.example.administrator.coolweather.gson.Weather;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.ResponseUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingpic();
        AlarmManager alarmclook = (AlarmManager) getSystemService(ALARM_SERVICE);
        int  anhour  =  60*60*1000;
        long time = SystemClock.elapsedRealtime() + anhour;
        Intent intent1 = new Intent(this, AutoUpdateService.class);
        PendingIntent service = PendingIntent.getService(this, 0, intent1, 0);
        alarmclook.cancel(service);
        alarmclook.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,service);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingpic() {
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                edit.putString("bing_pic",string);
                edit.apply();

            }
        });
    }

    private void updateWeather() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherstring = defaultSharedPreferences.getString("weather", null);
        if(weatherstring!=null){
            Weather weather = ResponseUtil.handleWeatherResponse(weatherstring);
            String weatherid = weather.basic.id;
            String  weatherUrl ="http://guolin.tech/api/weather=?cityid=" +weatherid + "&key=230a7be1e1ae4545b211c66d81ace031";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responsestring = response.body().string();
                    Weather weather1 = ResponseUtil.handleWeatherResponse(responsestring);
                    if(weather1!=null && "ok".equals(weather1.status)){
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        edit.putString("weather",responsestring);
                        edit.apply();
                    }
                }
            });

        }
    }
}
