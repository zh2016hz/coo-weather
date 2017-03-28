package com.example.administrator.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by aa on 17/3/28.
 * 省的对象数据
 */

public class Province extends DataSupport {
    private int provinceId;
    private String provinceName;
    private int provinceCode;

    public int getProvinceId() {
        return provinceId;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
