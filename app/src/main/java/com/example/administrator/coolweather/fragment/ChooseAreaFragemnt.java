package com.example.administrator.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.coolweather.MainActivity;
import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.WeatherActivty;
import com.example.administrator.coolweather.db.City;
import com.example.administrator.coolweather.db.Country;
import com.example.administrator.coolweather.db.Province;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.ResponseUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by aa on 17/3/29.
 */

public class ChooseAreaFragemnt extends Fragment {
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;
    private ArrayList<String> al = new ArrayList<>();
    private ListView ls;
    private int currentlevel;
    private List<Province> listprovince;
    private List<City> listcity;
    private List<Country> listcountry;
    private Province province;
    private City city;
    private Country country;
    private Button back;
    private TextView title;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        ls = (ListView) view.findViewById(R.id.listview);
        back = (Button) view.findViewById(R.id.back_button);
        title = (TextView) view.findViewById(R.id.title);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, al);
        ls.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentlevel == LEVEL_PROVINCE) {
                    province = listprovince.get(position);

                    queryCity();
                } else if (currentlevel == LEVEL_CITY) {
                    city = listcity.get(position);
                    queryCountry();
                } else if (currentlevel == LEVEL_COUNTY) {

                    String countryID = listcountry.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivty.class);
                        intent.putExtra("weather_id", countryID);
                        startActivity(intent);
                        getActivity().finish();

                    } else if (getActivity() instanceof WeatherActivty) {
                        WeatherActivty activity = (WeatherActivty) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipe.setRefreshing(true);
                        activity.requrestWeather(countryID);
                    }
                }

            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentlevel == LEVEL_COUNTY) {
                    queryCity();
                } else if (currentlevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });
        queryProvince();

    }

    private void queryProvince() {
        title.setText("中国");
        back.setVisibility(View.GONE);
        listprovince = DataSupport.findAll(Province.class);
        if (listprovince.size() > 0) {
            al.clear();
            for (Province province : listprovince
                    ) {
                al.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            ls.setSelection(0);
            currentlevel = LEVEL_PROVINCE;
        } else {
            String addres = "http://guolin.tech/api/china/";
            queryProvinceFromServers(addres, "province");
        }
    }

    private void queryCountry() {

        title.setText(city.getCityName());
        back.setVisibility(View.VISIBLE);
        listcountry = DataSupport.where("cityId =?", String.valueOf(city.getCityId())).find(Country.class);
        if (listcountry.size() > 0) {
            Log.e("", "queryCountry: " + "sssss");
            al.clear();
            for (Country contry : listcountry
                    ) {
                al.add(contry.getCountryName());
            }
            adapter.notifyDataSetChanged();
            ls.setSelection(0);
            currentlevel = LEVEL_COUNTY;
        } else {

            int provinceCode = province.getProvinceId();
            int cityCode = city.getCityId();
            String addrss = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryProvinceFromServers(addrss, "country");
        }


    }

    private void queryCity() {
        title.setText(province.getProvinceName());
        back.setVisibility(View.VISIBLE);

        listcity = DataSupport.where("provinceId =?", String.valueOf(province.getProvinceId())).find(City.class);
        if (listcity.size() > 0) {
            al.clear();
            for (City city : listcity
                    ) {
                al.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            ls.setSelection(0);
            currentlevel = LEVEL_CITY;
        } else {


            int provinceCode = province.getProvinceId();

            String addrss = "http://guolin.tech/api/china/" + provinceCode + "/";
            queryProvinceFromServers(addrss, "city");
        }


    }

    private void queryProvinceFromServers(String addres, final String type) {
        showProgressDailog();
        HttpUtil.sendOkHttpRequest(addres, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_LONG).show();
                    }
                }));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respon = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = ResponseUtil.handleProvinceResponse(respon);
                } else if ("city".equals(type)) {
                    result = ResponseUtil.handleCityResponse(respon, province.getProvinceId());
                } else if ("country".equals(type)) {
                    result = ResponseUtil.handleCountryResponse(respon, city.getCityId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDailog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCity();
                            } else if ("country".equals(type)) {
                                queryCountry();
                            }
                        }
                    }));
                }

            }
        });
    }

    private void closeProgressDailog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    private void showProgressDailog() {
        if (progress == null) {
            progress = new ProgressDialog(getContext());
            progress.setMessage("正在加载");
            progress.setCanceledOnTouchOutside(false);
        }
        progress.show();
    }


}
