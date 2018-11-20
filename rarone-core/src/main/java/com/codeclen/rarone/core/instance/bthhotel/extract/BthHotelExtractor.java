package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractHotelExtractor;
import com.codeclen.rarone.core.instance.Hotel;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author lin
 * @since 2018/11/16.
 */
public class BthHotelExtractor extends AbstractHotelExtractor {

    private static final String HOTEL_PAGE_URL = "http://www.bthhotels.com/hotel/%s";

    private BthHotelDetailExtractor hotelDetailExtractor = new BthHotelDetailExtractor();
    {
        this.responseType = ResponseType.HTML;
    }

    @Override
    public Page<Hotel> extract(String html) {
        Page<Hotel> page = new Page<>();
        List<Hotel> hotels = new LinkedList<>();
        page.setData(hotels);
        Document document = Jsoup.parse(html);
        //总条数
        String totalVal = document.select("#HotelList_Count").val();
        Integer total = StringUtils.isNotEmpty(totalVal) ? Integer.parseInt(totalVal) : 0;
        page.setTotalCount(total);
        //当前页码, 这个页面的pageNo不正常
//        String pageNoVal = document.select("#pageNo").val();
//        Integer pageNo = StringUtils.isNotEmpty(pageNoVal) ? Integer.parseInt(pageNoVal) : 0;
//        page.setPageNo(pageNo);
//        page.setPageNoParamName("pageNo");

        Elements elements = document.select(".list_boxline");
        elements.forEach(element -> {
            Elements introEle = element.select(".list_intro_txt");
            String hotelId = introEle.select(".list_intro_name_tj>a").attr("data-hotelcd");
            Elements nameEle = introEle.select(".list_intro_name_tj>a");
            Elements addressEle = introEle.select(".list_intro_address_tj>span");
            Elements facEles = introEle.select(".list_intro_icon>span");
            List<String> facList = new LinkedList<>();
            facEles.forEach(fac -> {
                String facVal = fac.attr("title");
                facList.add(facVal);
            });
            String location = element.attr("data-point");
            String hotelName = nameEle.attr("title");
            String address = addressEle.attr("title");
            Hotel hotel = new Hotel();
            hotel.setHid(hotelId);
            hotel.setName(hotelName);
            hotel.setAddress(address);
            hotelDetailExtractor.setUrl(String.format(HOTEL_PAGE_URL, hotelId));
            hotelDetailExtractor.extractHotel(null, hotel);
            if(hotel != null){
                hotel.setHid(hotelId);
                hotels.add(hotel);
            }
        });
        return page;
    }
}
