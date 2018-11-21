package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.codeclen.rarone.core.instance.bthhotel.pipeline.BthCommentPipeline;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/21.
 */
public class BthCommentExtractorTest {


    @Test
    public void extract() throws Exception {
        Map<String, Object> hotelParams = new HashMap<>();
        BthCommentExtractor bthCommentExtractor = new  BthCommentExtractor();
        hotelParams.put("hotelcd", "010088");
        hotelParams.put("pageNo",1);
        bthCommentExtractor.withUrl("http://www.bthhotels.com/HotelAct/dianping")
                .withPipeline(new BthCommentPipeline("010088"))
                .withParams(hotelParams)
                .withPageNo("pageNo", 1)
                .actionGet();
    }

}