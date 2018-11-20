package com.codeclen.rarone.core.instance;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author lin
 * @since 2018/11/14.
 */
public abstract class AbstractHotelDetailExtractor<Hotel> extends ExpandDetailExtractor{

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public abstract Hotel expand(String params, Hotel hotel);

    public Hotel extractHotel(Map<String,Object> params, Hotel hotel){
        if(StringUtils.isNotEmpty(url)){
            String response = request(url, params);
            if(response != null){
                return expand(response, hotel);
            }
        }
        return null;
    }
}
