package com.codeclen.rarone.core.instance.impl;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.Room;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelRoomExtractor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/14.
 */
public class BestwehotelRoomExtractorTest {

    @Test
    public void test(){
        BestwehotelRoomExtractor roomExtractor = new BestwehotelRoomExtractor();
        Map<String,Object> params = new HashMap<>();
        params.put("channelCode","CA00046");
        params.put("innId","5688");
        params.put("beginDate","2018-11-14");
        params.put("endDate","2018-11-15");
        params.put("days",1);
        params.put("brandId",137);
        params.put("langType","LANG_ZH");
        Page<Room> cityList = roomExtractor.run("http://hotel.bestwehotel.com/api/hotel/queryRoomRateList", params);
        cityList.getData().stream().forEach(city -> {
            System.out.println(JSON.toJSONString(city));
        });
    }

}
