package com.codeclen.rarone.core.instance;

public class RoomRate{

        /**
         * 入住时间
         */
        private Long arriveDateTime;

        /**
         * 退房时间
         */
        private Long leaveDateTime;

        /**
         * 入住状态：0-可预订，2-已满房
         */
        private Integer bookingType;
        /**
         * 是否包含早餐，0：无早餐，1：有早餐
         */
        private Integer breakfast;

        /**
         * 价格类型：会员价，门市价，APP特惠
         */
        private String rateName;

        /**
         * 价格
         */
        private Double sellingRate;

        public Long getArriveDateTime() {
            return arriveDateTime;
        }

        public void setArriveDateTime(Long arriveDateTime) {
            this.arriveDateTime = arriveDateTime;
        }

        public Long getLeaveDateTime() {
            return leaveDateTime;
        }

        public void setLeaveDateTime(Long leaveDateTime) {
            this.leaveDateTime = leaveDateTime;
        }

        public Integer getBookingType() {
            return bookingType;
        }

        public void setBookingType(Integer bookingType) {
            this.bookingType = bookingType;
        }

        public Integer getBreakfast() {
            return breakfast;
        }

        public void setBreakfast(Integer breakfast) {
            this.breakfast = breakfast;
        }

        public String getRateName() {
            return rateName;
        }

        public void setRateName(String rateName) {
            this.rateName = rateName;
        }

        public Double getSellingRate() {
            return sellingRate;
        }

        public void setSellingRate(Double sellingRate) {
            this.sellingRate = sellingRate;
        }
    }