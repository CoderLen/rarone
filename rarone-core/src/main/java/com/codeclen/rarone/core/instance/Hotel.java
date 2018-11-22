package com.codeclen.rarone.core.instance;

import java.util.List;

/**
 * @author lin
 * @since 2018/11/14.
 */
public class Hotel {

    public static class Coordinate{
        private double lat;

        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("[");
            sb.append(lat).append(",");
            sb.append(lng);
            sb.append(']');
            return sb.toString();
        }
    }


    private Long id;

    private String hid;

    private String name;

    private String describe;

    private String level;

    private String firstBrand;

    private String secondBrand;

    private String phone;


    private String address;

    private String city;

    private String email;

    private String policy;


    /**
     * 经纬度信息
     */
    private Coordinate coordinate;

    /**
     * 酒店设施
     */
    private List<Facility> facilities;

    /**
     * 酒店服务
     */
    private List<HotelService> services;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFirstBrand() {
        return firstBrand;
    }

    public void setFirstBrand(String firstBrand) {
        this.firstBrand = firstBrand;
    }

    public String getSecondBrand() {
        return secondBrand;
    }

    public void setSecondBrand(String secondBrand) {
        this.secondBrand = secondBrand;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<HotelService> getServices() {
        return services;
    }

    public void setServices(List<HotelService> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("hid='").append(hid).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", phone='").append(phone).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
