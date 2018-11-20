package com.codeclen.rarone.core.pipeline.impl;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.Hotel;
import com.codeclen.rarone.core.instance.Room;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelRoomExtractor;
import com.codeclen.rarone.core.pipeline.Pipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/15.
 */
public class BestwehotelHotelPipeline implements Pipeline<Hotel> {

    private BestwehotelRoomExtractor roomExtractor = new BestwehotelRoomExtractor();

    @Override
    public void process(Hotel a) {
        System.out.println(JSON.toJSONString(a));
    }

    @Override
    public void process(Collection<Hotel> collection) {
        collection.stream().forEach(hotel -> {
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
            System.out.println(hotel.getName());
            rooms.getData().stream().forEach(room -> {
                room.setHotelName(hotel.getName());
                System.out.println("    ------ " + room.getRoomType() + ":" + room.getMaxCheckin());
            });
        });
    }
}
