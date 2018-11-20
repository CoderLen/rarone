package com.codeclen.rarone.core.instance.bthhotel;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.bthhotel.extract.BthCityExtractor;
import org.junit.Test;

/**
 * @author lin
 * @since 2018/11/16.
 */
public class BthCityExtractorTest {

    @Test
    public void testBthCity(){
        BthCityExtractor cityExtractor = new BthCityExtractor();
        Page<City> cityList = cityExtractor.run("http://www.bthhotels.com/Ajax/GetCity_All?ts=0.20440320835447356");
        cityList.getData().forEach(city -> System.out.println(JSON.toJSONString(city)));
    }

}
