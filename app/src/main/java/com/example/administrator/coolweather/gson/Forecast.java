package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aa on 17/3/29.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt_d")
        public String  info;
    }
    public class Temperature {
        public String  max;
        public String  min;


    }
}
