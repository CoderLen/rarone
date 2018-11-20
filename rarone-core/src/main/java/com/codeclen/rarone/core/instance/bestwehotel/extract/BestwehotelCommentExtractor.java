package com.codeclen.rarone.core.instance.bestwehotel.extract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractRoomExtractor;
import com.codeclen.rarone.core.instance.Comment;

import java.util.LinkedList;
import java.util.List;

/**
 * http://hotel.bestwehotel.com/api/hotel/queryHotelDetail
 * @author lin
 * @since 2018/11/14.
 */
public class BestwehotelCommentExtractor extends AbstractRoomExtractor {


    @Override
    public Page<Comment> extract(String json) {
        List<Comment> res = new LinkedList<>();
        Page<Comment> page = new Page<>();
        page.setData(res);
        try{
            if(json != null){
                JSONObject object = JSONObject.parseObject(json);
                if(object.containsKey("data")){
                    JSONObject dataObj = object.getJSONObject("data");
                    if(dataObj.containsKey("comments")){
                        JSONArray commentArr = dataObj.getJSONArray("comments");
                        for(int i=0; commentArr != null && i < commentArr.size(); i++){
                            JSONObject commentObj = commentArr.getJSONObject(i);
                            Comment comment = new Comment();
                            comment.setComment(commentObj.getString("content"));
                            comment.setNickname(commentObj.getString("memberNickName"));
                            comment.setScore(commentObj.getDouble("avgScore"));
                            comment.setCreateTime(commentObj.getString("createTime"));
                            JSONArray childrenEvalutions = commentObj.getJSONArray("childrenEvaluations");
                            if(childrenEvalutions != null && childrenEvalutions.size() > 0){
                                JSONObject replyObj = childrenEvalutions.getJSONObject(0);
                                Long replyTime = replyObj.getLong("createTime");
                                if(replyTime != null){
                                    comment.setReplyTime(replyTime);
                                }
                            }
                            comment.setEntireScore(commentObj.getInteger("entireScore"));
                            res.add(comment);
                        }
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return page;
    }

}
