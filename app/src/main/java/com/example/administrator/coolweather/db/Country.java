package com.example.administrator.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by aa on 17/3/28.
 */

public class Country extends DataSupport {
    private int countryID;
    private String countryName;
    private int weatherId;
    private int cityId;

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
