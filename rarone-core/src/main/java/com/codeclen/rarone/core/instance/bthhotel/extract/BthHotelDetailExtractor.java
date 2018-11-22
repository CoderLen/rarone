package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.codeclen.rarone.core.instance.AbstractHotelDetailExtractor;
import com.codeclen.rarone.core.instance.Facility;
import com.codeclen.rarone.core.instance.Hotel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.LinkedList;
import java.util.List;

/**
 * 首旅如家的酒店，不同等级的酒店的模板有点区别，需要特殊处理
 * @author lin
 * @since 2018/11/16.
 */
@Slf4j
public class BthHotelDetailExtractor extends AbstractHotelDetailExtractor<Hotel> {

    private static final String LOWCATE = "经济型";
    private static final String HIGHCATE = "高端";

    {
        this.responseType = ResponseType.HTML;
    }

    public BthHotelDetailExtractor() {
    }

    @Override
    public Hotel expand(String html, Hotel hotel) {
        if(hotel != null){
            Document doc = Jsoup.parse(html);
            List<Hotel> hotels = new LinkedList<> ();
            hotels.add(hotel);
            //酒店类型：经济型，高端
            String primaryCate = doc.select("#hotelPrimaryCategory_pVar").val();
            hotel.setLevel(primaryCate);
            //酒店品牌
            String brand = doc.select("#hotelSecondaryCategory_pVar").val();
            hotel.setSecondBrand(brand);
            if (LOWCATE.equalsIgnoreCase(primaryCate)) {
                String facStrs = doc.select("body > div.main_pc > div:nth-child(3) > div.list_introduce.main_w1200 > div.list_introduce_l.fl > div.list_intro_text.fix").text();
                if(StringUtils.isNotEmpty(facStrs)){
                    String[] facArr = facStrs.split("\\s+");
                    List<Facility> facilities = new LinkedList<>();
                    for(String fac : facArr){
                        if(StringUtils.isNotEmpty(fac)){
                            Facility facility = new Facility();
                            facility.setName(fac);
                            facility.setHotelId(hotel.getHid());
                            facilities.add(facility);
                        }
                    }
                    hotel.setFacilities(facilities);
                }
                String phone = doc.select("#information > div:nth-child(2)").text();
                String descript = doc.select("#information > div:nth-child(3)").text();
                String tips = doc.select("#information > div:nth-child(4)").text();
                hotel.setPolicy(tips);
                hotel.setDescribe(descript);
                hotel.setPhone(phone);
            }else if(HIGHCATE.equalsIgnoreCase(primaryCate)){
                String address = doc.select("body > div.main_pc > div.main > div.content_JG.main_w1200 > div.header_Room.fix > div.header_Room_l.fl > p:nth-child(2)").text();
                hotel.setAddress(address);
                String introduce = doc.select("body > div.main_pc > div.popup_mask.Hotel_Details_mask > p.Room_introduction").text();
                hotel.setDescribe(introduce);
                String phone = doc.select("body > div.main_pc > div.main > div.content_JG.main_w1200 > div.header_Room.fix > div.header_Room_l.fl > p:nth-child(3)").text();
                hotel.setPhone(phone);
                //提示
                String tips = doc.select("body > div.main_pc > div.popup_mask.Hotel_Details_mask > p:nth-child(5)").text();
                //酒店特殊
                String special = doc.select("body > div.main_pc > div.popup_mask.Hotel_Details_mask > p:nth-child(7)").text();

            }else {
                log.error("Unknown Primary Category: " + primaryCate);
            }
        }
        return hotel;
    }
}
