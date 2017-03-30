package com.example.administrator.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.coolweather.gson.Forecast;
import com.example.administrator.coolweather.gson.Weather;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.ResponseUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivty extends AppCompatActivity {

    private ScrollView scrollView;
    private TextView title;
    private TextView update;
    private TextView degree;
    private TextView weather_info;
    private TextView aqi;
    private TextView pm;
    private TextView comfort;
    private TextView car_wash;
    private TextView sport;
    private LinearLayout forest;
    private ImageView backgraoud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        setContentView(R.layout.activity_weather_activty);
        backgraoud = (ImageView) findViewById(R.id.background);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        title = (TextView) findViewById(R.id.titlename);
        update = (TextView) findViewById(R.id.updatetime);
        degree = (TextView) findViewById(R.id.degree);
        weather_info = (TextView) findViewById(R.id.weather_info);
        aqi = (TextView) findViewById(R.id.aqi);
        pm = (TextView) findViewById(R.id.pm);
        comfort = (TextView) findViewById(R.id.comfort);
        car_wash = (TextView) findViewById(R.id.car_wash);
        sport = (TextView) findViewById(R.id.sport);
        forest = (LinearLayout) findViewById(R.id.forest);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = defaultSharedPreferences.getString("weather", null);
        if (defaultSharedPreferences.getString("ping_pic", null) != null) {

            Glide.with(this).load(defaultSharedPreferences.getString("ping_pic", null)).into(backgraoud);
        } else {

            loadBackground();
        }
        if (weather != null) {
            Weather weather1 = ResponseUtil.handleWeatherResponse(weather);
            showWeatherinfo(weather1);

        } else {
            String weather_id = getIntent().getStringExtra("weather_id");
            scrollView.setVisibility(View.INVISIBLE);

            requrestWeather(weather_id);
        }
    }

    private void loadBackground() {


        final String urlback = "http://guolin.tech/api/bing_pic";


        Log.e("adderes", "queryProvinceFromServers: " + "#####");
        HttpUtil.sendOkHttpRequest(urlback, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivty.this, "获取背景失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivty.this).edit();
                SharedPreferences.Editor ping_pic = edit.putString("ping_pic", result);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("adderes", "queryProvinceFromServers: " + "rrrrrr" + result);
                        Glide.with(WeatherActivty.this).load(result).into(backgraoud);
                    }
                });
                Log.e("adderes", "queryProvinceFromServers: " + "当前是省ssss");
            }
        });
    }


    private void requrestWeather(String weather_id) {

        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=230a7be1e1ae4545b211c66d81ace031";//TODO

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivty.this, "联网失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respon = response.body().string();
                final Weather weather = ResponseUtil.handleWeatherResponse(respon);
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (weather != null && "ok".equals(weather.status)) {
                                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivty.this).edit();
                                    edit.putString("weather", respon);
                                    edit.apply();
                                    showWeatherinfo(weather);
                                } else {
                                    Toast.makeText(WeatherActivty.this, "获取天气失败", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                loadBackground();
            }
        });


    }

    private void showWeatherinfo(Weather weather) {
        String city = weather.basic.city;
        String updatetime = weather.basic.update.updatetime;
        String tmp = weather.now.tmp;
        String txt = weather.now.more.txt;//多云
        String comfort1 = "舒适度：  " + weather.suggestion.comfort.txt;//舒适度
        String carwash = "洗车建议：  " + weather.suggestion.carwash.txt;//洗车
        String sport1 = "运动建议：  " + weather.suggestion.sport.txt;//运动

        title.setText(city);
        update.setText(updatetime);
        degree.setText(tmp);
        weather_info.setText(txt);
        forest.removeAllViews();

        for (Forecast fore : weather.forecastList
                ) {
            View view = LayoutInflater.from(this).inflate(R.layout.forest_item, forest, false);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView info = (TextView) view.findViewById(R.id.info);
            TextView max = (TextView) view.findViewById(R.id.max);
            TextView min = (TextView) view.findViewById(R.id.min);

            date.setText(fore.date);
            info.setText(fore.more.info);
            max.setText(fore.temperature.max);
            min.setText(fore.temperature.min);
            forest.addView(view);
        }
        Log.e("adderes", "queryProvinceFromServers: " + "当前是省" + weather.aqi);
        if (weather.aqi != null) {

            aqi.setText(weather.aqi.city.aqi);
            pm.setText(weather.aqi.city.pm25);
        }
        car_wash.setText(carwash);
        comfort.setText(comfort1);
        sport.setText(sport1);
        scrollView.setVisibility(View.VISIBLE);

    }

}
