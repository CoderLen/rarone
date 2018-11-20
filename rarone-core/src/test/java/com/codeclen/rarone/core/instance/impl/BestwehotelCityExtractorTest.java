package com.codeclen.rarone.core.instance.impl;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelCityExtractor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/14.
 */
public class BestwehotelCityExtractorTest {


    @Test
    public void test(){
        BestwehotelCityExtractor cityExtractor = new BestwehotelCityExtractor();
        Map<String,Object> params = new HashMap<>();
        params.put("channelCode","CA00003");
        Page<City> cityList = cityExtractor.run("http://hotel.bestwehotel.com/api/hotel/queryAllCites",params);
        cityList.getData().stream().forEach(city -> {
            System.out.println(JSON.toJSONString(city));
        });
    }

}
