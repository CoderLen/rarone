package com.codeclen.rarone.core.instance.bthhotel.pipeline;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.instance.Comment;
import com.codeclen.rarone.core.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author lin
 * @since 2018/11/21.
 */
@Slf4j
public class BthCommentPipeline implements Pipeline<Comment> {

    private String hotelId;

    public BthCommentPipeline(String hotelId){
        this.hotelId = hotelId;
    }

    @Override
    public void process(Comment a) {
        a.setHotelId(hotelId);
        System.out.println(JSON.toJSONString(a));
        log.info("------------------------");
    }

    @Override
    public void process(Collection<Comment> collection) {
        collection.forEach(a -> process(a));
    }
}
