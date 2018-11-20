package com.codeclen.rarone.core.instance.bthhotel;

import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.bthhotel.extract.BthCityExtractor;
import com.codeclen.rarone.core.instance.bthhotel.extract.BthHotelExtractor;
import com.codeclen.rarone.core.pipeline.HotelPrintPipeline;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/16.
 */
public class BthHotelExtractorTest {


    @Test
    public void extract() throws Exception {
        BthCityExtractor cityExtractor = new BthCityExtractor();
        Page<City> cityList = cityExtractor.run("http://www.bthhotels.com/Ajax/GetCity_All?ts=0.20440320835447356");
        cityList.getData().parallelStream().forEach(city -> {
//            System.out.println(JSON.toJSONString(city));
            Map<String, Object> hotelParams = new HashMap<>();
            hotelParams.put("cityCode",city.getCityCode());
            hotelParams.put("cityName",city.getCityName());
            hotelParams.put("beginDate","2018/11/19");
            hotelParams.put("endDate","2018/11/20");
            hotelParams.put("pageNo",1);
            hotelParams.put("pageSize",10);
            BthHotelExtractor bthHotelExtractor = new BthHotelExtractor();
            bthHotelExtractor.withUrl("http://www.bthhotels.com/listasync")
                    .withPipeline(new HotelPrintPipeline())
                    .withParams(hotelParams)
                    .withPageNo("pageNo", 10)
                    .actionGet();
        });

    }

}