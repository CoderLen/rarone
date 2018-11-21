package com.codeclen.rarone.core.instance;

import lombok.Data;

/**
 * @author lin
 * @since 2018/11/15.
 */
@Data
public class Comment {

    private String hotelId;

    private String comment;

    private Double score;

    private String createTime;

    private String nickname;

    /**
     * 酒店回复
     */
    private String hotelReply;

    private Long replyTime;

    /**
     * 评论类型，0：好评，1:中评，2：差评
     */
    private Double entireScore;

}
