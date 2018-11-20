package com.codeclen.rarone.core.instance.impl;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.Comment;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelCommentExtractor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/15.
 */
public class BestwehotelCommentExtractorTest {

    @Test
    public void extract(){
        BestwehotelCommentExtractor commentExtractor = new BestwehotelCommentExtractor();
        Map<String,Object> params = new HashMap<>();
        params.put("merchantId", 5525);
        params.put("sortType", 0);
        int pageNo = 1;
        do{
            Map<String,Integer> pageParams = new HashMap<>();
            pageParams.put("pageNo", pageNo);
            pageParams.put("pageSize", 10);
            params.put("page", pageParams);
            Page<Comment> comments = commentExtractor.run("http://hotel.bestwehotel.com/api/comment/queryCommentList",params);
            if(comments.getData().size() > 0){
                comments.getData().forEach(comment -> {
                    System.out.println(JSON.toJSONString(comment));
                });
                pageNo++;
            }else {
                pageNo = 0;
            }
        }while (pageNo > 0);

    }


}