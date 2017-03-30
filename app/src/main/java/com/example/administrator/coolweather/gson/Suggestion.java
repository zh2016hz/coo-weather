package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aa on 17/3/29.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public Carwash carwash;
    public Sport sport;
    public class Comfort {
        public String txt;
    }

    public class Carwash {
        public String txt;
    }

    public class Sport {
        public String txt;
    }
}
