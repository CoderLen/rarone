package com.codeclen.rarone.core.instance;

import com.codeclen.rarone.core.City;

import java.util.List;

/**
 * @author lin
 * @since 2018/11/14.
 */
public abstract class HotelCawler {

    public abstract List<City> queryCity();

    public abstract List<Hotel> queryHotel(City city);

    public abstract List<HotelRoom> queryRoom(Hotel hotel);

    public abstract List<Comment> queryComment(Hotel hotel);


    public void run(){
        List<City> cities = queryCity();
        cities.stream().forEach(city -> {
            List<Hotel> hotels = queryHotel(city);
            hotels.stream().forEach(hotel -> {
                List<HotelRoom> hotelRooms = queryRoom(hotel);
                List<Comment> hotelComments = queryComment(hotel);
            });
        });
    }
}
