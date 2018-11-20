package com.codeclen.rarone.core.instance.bestwehotel.extract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codeclen.rarone.core.instance.*;

import java.util.LinkedList;
import java.util.List;

/**
 * http://hotel.bestwehotel.com/api/hotel/queryHotelDetail
 * @author lin
 * @since 2018/11/14.
 */
public class BestwehotelHotelDetailExtractor extends AbstractHotelDetailExtractor<Hotel> {

    public BestwehotelHotelDetailExtractor(String url) {
        super.setUrl(url);
    }

    @Override
    public Hotel expand(String json, Hotel hotel) {
        try{
            if(json != null && hotel != null){
                JSONObject object = JSONObject.parseObject(json);
                if(object.containsKey("data")){
                    JSONObject datajson = object.getJSONObject("data");
                    if(datajson.containsKey("hotel")){
                        JSONObject hotelObj = datajson.getJSONObject("hotel");
                        JSONObject innNameObj = hotelObj.getJSONObject("innName");
                        String hotelName = innNameObj.getString("0");
                        hotel.setName(hotelName);
                        JSONObject addressObj = hotelObj.getJSONObject("address");
                        String address = addressObj.getString("0");
                        hotel.setAddress(address);
                        JSONObject descObj = hotelObj.getJSONObject("description");
                        String description = descObj.getString("0");
                        hotel.setDescribe(description);
                        String phone = hotelObj.getString("innPhone");
                        hotel.setPhone(phone);
                        String level = hotelObj.getString("innLevel");
                        hotel.setLevel(level);
                        hotel.setHid(hotelObj.getString("innId"));
                        hotel.setFacilities(extractFacilities(datajson));
                        hotel.setServices(extractServices(datajson));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hotel;
    }


    /**
     * 提取酒店设施列表
     * @param json
     * @return
     */
    private List<Facility> extractFacilities(JSONObject json) {
        List<Facility> facilities = new LinkedList<>();
        if(json.containsKey("facilities")){
            JSONObject jsonObject = json.getJSONObject("facilities");
            if(jsonObject.containsKey("facList")){
                JSONArray facArray = jsonObject.getJSONArray("facList");
                for(int i = 0; facArray != null && i < facArray.size(); i++){
                    JSONObject facObj = facArray.getJSONObject(i);
                    Facility facility = new Facility();
                    facility.setCode(facObj.getString("facCode"));
                    JSONObject facNameObj = facObj.getJSONObject("facName");
                    facility.setName(facNameObj.getString("0"));
                    facility.setRemark(facObj.getString("remark"));
                    facility.setType(Integer.parseInt(facObj.getString("facType")));
                    facility.setValid(facObj.getInteger("valid") == 1);
                    facility.setHotelId(facObj.getString("innId"));
                    facilities.add(facility);
                }
            }
        }
        return facilities;
    }

    /**
     * 提取酒店服务列表
     * @param json
     * @return
     */
    private List<HotelService> extractServices(JSONObject json) {
        List<HotelService> services = new LinkedList<>();
        if(json.containsKey("facilities")){
            JSONObject jsonObject = json.getJSONObject("facilities");
            if(jsonObject.containsKey("serList")){
                JSONArray facArray = jsonObject.getJSONArray("serList");
                for(int i = 0; facArray != null && i < facArray.size(); i++){
                    JSONObject facObj = facArray.getJSONObject(i);
                    HotelService service = new HotelService();
                    service.setCode(facObj.getString("serCode"));
                    JSONObject facNameObj = facObj.getJSONObject("serName");
                    service.setName(facNameObj.getString("0"));
                    service.setValid(facObj.getInteger("valid") == 1);
                    service.setHotelId(facObj.getString("innId"));
                    //todo other fileds, createdAt etc
                    services.add(service);
                }
            }
        }
        return services;
    }
}
