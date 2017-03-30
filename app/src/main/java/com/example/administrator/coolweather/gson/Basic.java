package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aa on 17/3/29.
 */

public class Basic {
    public String city;
    public String id;
    public Update update;
    public class Update {
        @SerializedName("loc")
        public String updatetime;
    }
}
