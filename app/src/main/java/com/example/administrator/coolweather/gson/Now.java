package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aa on 17/3/29.
 */

public class Now {
    public String tmp;
    @SerializedName("cond")
    public More more;
    public class More{
        public String txt;
    }
}
