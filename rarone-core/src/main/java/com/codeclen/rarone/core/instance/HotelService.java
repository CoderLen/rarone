package com.codeclen.rarone.core.instance;

import java.util.Date;

/**
 * 酒店服务
 * @author lin
 * @since 2018/11/14.
 */
public class HotelService {

    private String name;

    private String code;

    private Date createdAt;

    private Boolean valid;

    private String hotelId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
}
