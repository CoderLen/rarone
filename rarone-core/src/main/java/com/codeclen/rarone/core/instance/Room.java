package com.codeclen.rarone.core.instance;

import java.util.List;

/**
 * 房间信息
 * @author lin
 * @since 2018/11/14.
 */
public class Room {

    /**
     * 酒店名称
     */
    private String hotelName;

    /**
     * 酒店id
     */
    private String hotelId;

    /**
     * 房型
     */
    private String roomType;

    /**
     * 房间大小：etc. 20-27
     */
    private String roomSize;

    /**
     * 最大入住人数
     */
    private Integer maxCheckin;

    /**
     * 是否有wifi
     */
    private Integer wifi;

    /**
     * 是否有窗
     */
    private Integer window;

    private String image;

    private List<RoomRate> roomRates;

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(String roomSize) {
        this.roomSize = roomSize;
    }

    public Integer getMaxCheckin() {
        return maxCheckin;
    }

    public void setMaxCheckin(Integer maxCheckin) {
        this.maxCheckin = maxCheckin;
    }

    public Integer getWifi() {
        return wifi;
    }

    public void setWifi(Integer wifi) {
        this.wifi = wifi;
    }

    public Integer getWindow() {
        return window;
    }

    public void setWindow(Integer window) {
        this.window = window;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
