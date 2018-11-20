package com.codeclen.rarone.core.instance;

/**
 * @author lin
 * @since 2018/11/15.
 */
public class Comment {

    private String hotelId;

    private String comment;

    private Double score;

    private String createTime;

    private String nickname;

    private Long replyTime;

    /**
     * 评论类型，0：好评，1:中评，2：差评
     */
    private Integer entireScore;

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Long replyTime) {
        this.replyTime = replyTime;
    }

    public Integer getEntireScore() {
        return entireScore;
    }

    public void setEntireScore(Integer entireScore) {
        this.entireScore = entireScore;
    }
}
