package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractCityExtractor;

import java.util.LinkedList;
import java.util.List;

/**
 * @author lin
 * @since 2018/11/16.
 */
public class BthCityExtractor extends AbstractCityExtractor {


    @Override
    public Page<City> extract(String json) {
        Page<City> page = new Page();
        List<City> res = new LinkedList<>();
        page.setData(res);
        try{
            if(json != null){
                JSONObject object = JSONObject.parseObject(json);
                if(object.containsKey("data")){
                    JSONArray cityArr = object.getJSONArray("data");
                    for(int i=0; cityArr != null && i<cityArr.size();i++){
                        JSONObject cityObj = cityArr.getJSONObject(i);
                        City city = new City();
                        city.setCityCode(cityObj.getString("CD"));
                        city.setCityName(cityObj.getString("Descript"));
                        city.setPinyin(cityObj.getString("Pinyin"));
                        city.setFirstLetter(cityObj.getString("FirstPinyin"));
                        res.add(city);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return page;
    }
}
