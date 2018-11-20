package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.Room;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/19.
 */
public class BthRoomExtractorTest {



    @Test
    public void test(){
        BthRoomExtractor bthRoomExtractor = new BthRoomExtractor();
        Map<String,Object> params = new HashMap<>();
        params.put("ArrDate","2018-11-20");
        params.put("DepDate","2018-11-21");
        params.put("hotelCd","020063");
        params.put("PriceType", "");
        params.put("memberNo", "");

        Page<Room> cityList = bthRoomExtractor.run("http://www.bthhotels.com/HotelAct/HotelRomList", params);
        cityList.getData().stream().forEach(city -> {
            System.out.println(JSON.toJSONString(city));
        });
    }
}