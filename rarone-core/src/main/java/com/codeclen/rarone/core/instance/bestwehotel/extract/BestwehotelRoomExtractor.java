package com.codeclen.rarone.core.instance.bestwehotel.extract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractRoomExtractor;
import com.codeclen.rarone.core.instance.Room;
import com.codeclen.rarone.core.instance.RoomRate;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * http://hotel.bestwehotel.com/api/hotel/queryHotelDetail
 * @author lin
 * @since 2018/11/14.
 */
@Slf4j
public class BestwehotelRoomExtractor extends AbstractRoomExtractor {


    @Override
    public Page<Room> extract(String json) {
        Page<Room> page = new Page<>();
        List<Room> res = new LinkedList<>();
        page.setData(res);
        try{
            if(json != null){
                JSONObject object = JSONObject.parseObject(json);
                if(object.containsKey("data")){
                    JSONArray roomDatas = object.getJSONArray("data");
                    for(int i=0; roomDatas != null && i < roomDatas.size(); i++){
                        JSONObject roomobj = roomDatas.getJSONObject(i);
                        Room room = new Room();
                        room.setHotelId(roomobj.getString("innId"));
                        room.setRoomType(roomobj.getJSONObject("roomTypeName").getString("0"));
                        room.setMaxCheckin(roomobj.getInteger("maxCheckIn"));
                        room.setRoomSize(roomobj.getString("area"));
                        room.setWindow(roomobj.getInteger("window"));
                        room.setRoomRates(extractRoomRates(roomobj));
                        res.add(room);
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return page;
    }


    public List<RoomRate> extractRoomRates(JSONObject roomObj){
        List<RoomRate> roomRates = new LinkedList<>();
        if(roomObj.containsKey("rate")){
            JSONArray roomrates = roomObj.getJSONArray("rate");
            for(int i = 0; i < roomrates.size(); i++){
                JSONObject rateObj = roomrates.getJSONObject(i);
                RoomRate roomRate = new RoomRate();
                roomRate.setRateName(rateObj.getString("roomRateName"));
                roomRate.setBookingType(rateObj.getInteger("bookingType"));
                roomRate.setSellingRate(rateObj.getDouble("sellingRate"));
                roomRate.setBreakfast(rateObj.get("breakfast") != null ? 1 : 0);
                JSONObject liveRule = rateObj.getJSONObject("liveRule");
                roomRate.setArriveDateTime(liveRule.getLong("arriveDateTime"));
                roomRate.setLeaveDateTime(liveRule.getLong("leaveDateTime"));
                roomRates.add(roomRate);
            }
        }
        return roomRates;
    }

}
