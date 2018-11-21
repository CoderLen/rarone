package com.codeclen.rarone.core.instance.bthhotel.pipeline;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.Hotel;
import com.codeclen.rarone.core.instance.Room;
import com.codeclen.rarone.core.instance.bthhotel.extract.BthRoomExtractor;
import com.codeclen.rarone.core.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author lin
 * @since 2018/11/21.
 */
@Slf4j
public class BthHotelPipeline implements Pipeline<Hotel> {

    private static final String HOTEL_INFO_URL = "http://www.bthhotels.com/hotel/%s";
    private BthRoomExtractor bthRoomExtractor = new BthRoomExtractor();

    @Override
    public void process(Hotel a) {
        String url = String.format(HOTEL_INFO_URL, a.getHid());
        Page<Room> rooms = bthRoomExtractor.run(url);
        log.info(JSON.toJSONString(a));
        rooms.getData().forEach(room -> System.out.println(JSON.toJSONString(room)));
        log.info("------------------------");
    }

    @Override
    public void process(Collection<Hotel> collection) {
        collection.forEach(a -> process(a));
    }
}
