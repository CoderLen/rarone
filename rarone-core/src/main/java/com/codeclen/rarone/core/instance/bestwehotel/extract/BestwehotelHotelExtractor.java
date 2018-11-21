package com.codeclen.rarone.core.instance.bestwehotel.extract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractHotelDetailExtractor;
import com.codeclen.rarone.core.instance.AbstractHotelExtractor;
import com.codeclen.rarone.core.instance.Hotel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * http://hotel.bestwehotel.com/api/hotel/queryHotelDetail
 * @author lin
 * @since 2018/11/14.
 */
@Slf4j
public class BestwehotelHotelExtractor extends AbstractHotelExtractor {

    private AbstractHotelDetailExtractor<Hotel> hotelDetailExtractor = new BestwehotelHotelDetailExtractor("http://hotel.bestwehotel.com/api/hotel/queryHotelDetail");


    @Override
    public Page<Hotel> extract(String json) {
        Page<Hotel> page = new Page<>();
        List<Hotel> res = new LinkedList<>();
        page.setData(res);
        try{
            if(json != null){
                JSONObject object = JSONObject.parseObject(json);
                if(object.containsKey("data")){
                    JSONObject datajson = object.getJSONObject("data");
                    if(datajson.containsKey("hotels")){
                        JSONArray hotelsArray = datajson.getJSONArray("hotels");
                        for(int i = 0; hotelsArray != null && i < hotelsArray.size(); i++){
                            JSONObject hotelObj = hotelsArray.getJSONObject(i);
                            String hotelId = hotelObj.getString("hotelId");
                            Map<String,Object> params = new HashMap<>();
                            params.put("channelCode", "CA00046");
                            params.put("hotelId", hotelId);
                            Hotel hotel = new Hotel();
                            hotel.setHid(hotelId);
                            hotelDetailExtractor.extractHotel(params, hotel);
                            res.add(hotel);
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return page;
    }


}
