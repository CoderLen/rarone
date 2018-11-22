package com.codeclen.rarone.core.instance.bthhotel;

import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.bthhotel.extract.BthCityExtractor;
import com.codeclen.rarone.core.instance.bthhotel.extract.BthHotelExtractor;
import com.codeclen.rarone.core.instance.bthhotel.pipeline.BthHotelPipeline;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
        cityList.getData().forEach(city -> {
//            System.out.println(JSON.toJSONString(city));
            XSSFWorkbook workbook = new XSSFWorkbook();
            Map<String, Object> hotelParams = new HashMap<>();
            hotelParams.put("cityCode",city.getCityCode());
            hotelParams.put("cityName",city.getCityName());
            hotelParams.put("beginDate","2018/11/22");
            hotelParams.put("endDate","2018/11/23");
            hotelParams.put("pageNo",1);
            hotelParams.put("pageSize",10);
            BthHotelExtractor bthHotelExtractor = new BthHotelExtractor();
            bthHotelExtractor.withUrl("http://www.bthhotels.com/listasync")
                    .withPipeline(new BthHotelPipeline(workbook, city.getCityName()))
                    .withParams(hotelParams)
                    .withPageNo("pageNo", 10)
                    .actionGet();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File("G:\\test\\BthHotels_"+city.getCityName()+".xlsx"));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                workbook.write(bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

}