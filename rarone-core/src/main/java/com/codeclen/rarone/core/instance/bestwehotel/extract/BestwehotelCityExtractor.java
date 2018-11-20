package com.codeclen.rarone.core.instance.bestwehotel.extract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractCityExtractor;

import java.util.LinkedList;
import java.util.List;

/**
 * @author lin
 * @since 2018/11/14.
 */
public class BestwehotelCityExtractor extends AbstractCityExtractor {

    @Override
    public Page<City> extract(String json) {
        Page<City> page = new Page<>();
        List<City> cities = new LinkedList<>();
        try{
            if(json != null){
                JSONObject object = JSONObject.parseObject(json);
                if(object.containsKey("data")){
                    JSONObject datajson = object.getJSONObject("data");
                    if(datajson.containsKey("otaCitys")){
                        JSONArray jsonArray = datajson.getJSONArray("otaCitys");
                        for(int i = 0; i < jsonArray.size(); i++){
                            JSONObject cityObj = jsonArray.getJSONObject(i);
                            String cityName = cityObj.getString("cityName");
                            String cityCode = cityObj.getString("cityCode");
                            String pinyin = cityObj.getString("pinyin");
                            String firstLetter = cityObj.getString("firstLetter");
                            String lat = cityObj.getString("lat");
                            String lng = cityObj.getString("lng");
                            City city = new City();
                            city.setCityCode(cityCode);
                            city.setCityName(cityName);
                            city.setPinyin(pinyin);
                            city.setFirstLetter(firstLetter);
                            city.setLat(lat);
                            city.setLng(lng);
                            cities.add(city);
                        }
                    }
                }
            }
        }catch (Exception e){

        }
        page.setData(cities);
        return page;
    }
}
