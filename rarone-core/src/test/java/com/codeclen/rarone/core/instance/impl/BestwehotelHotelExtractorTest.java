package com.codeclen.rarone.core.instance.impl;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.Hotel;
import com.codeclen.rarone.core.instance.Room;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelCityExtractor;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelHotelExtractor;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelRoomExtractor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/14.
 */
public class BestwehotelHotelExtractorTest {

    @Test
    public void test(){
        BestwehotelHotelExtractor hotelExtractor = new BestwehotelHotelExtractor();
        BestwehotelRoomExtractor roomExtractor = new BestwehotelRoomExtractor();
        BestwehotelCityExtractor cityExtractor = new BestwehotelCityExtractor();
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("channelCode","CA00003");
        Page<City> cityList = cityExtractor.run("http://hotel.bestwehotel.com/api/hotel/queryAllCites", paramsMap);
        cityList.getData().stream().forEach(city -> {
            System.out.println("------ City: " + city.getCityName() + " ----");
            Map<String,Object> params = new HashMap<>();
            params.put("channelCode","CA00046");
            params.put("page",1);
            params.put("size",10);
            params.put("cityCode", city.getCityCode());
            params.put("checkInDate","2018-11-14");
            Page<Hotel> hotels = hotelExtractor.run("http://hotel.bestwehotel.com/api/hotel/searchHotels", params);
            hotels.getData().stream().forEach(hotel -> {
                Map<String,Object> romeparams = new HashMap<>();
                romeparams.put("channelCode","CA00046");
                romeparams.put("innId",hotel.getHid());
                romeparams.put("beginDate","2018-11-15");
                romeparams.put("endDate","2018-11-16");
                romeparams.put("days",1);
                romeparams.put("brandId",137);
                romeparams.put("langType","LANG_ZH");
//                System.out.println(JSON.toJSONString(hotel));
                Page<Room> rooms = roomExtractor.run("http://hotel.bestwehotel.com/api/hotel/queryRoomRateList", romeparams);
                rooms.getData().stream().forEach(room -> {
                    room.setHotelName(hotel.getName());
                    System.out.println(JSON.toJSONString(room));
                });
                System.out.println("Room Count: " + rooms.getData().size());
            });
            System.out.println("Hotel Count: " + hotels.getData().size());
            System.out.println("-------------------------------------");
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        });



    }
}
