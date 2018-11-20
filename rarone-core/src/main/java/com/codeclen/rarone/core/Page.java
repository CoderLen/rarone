package com.codeclen.rarone.core;

import java.util.List;

/**
 * @author lin
 * @since 2018/11/14.
 */
public class Page<T> {


    public static final String DEFAULT_PAGE_NO_PARAM_NAME = "pageNo";

    private Integer pageNo = 1;

    /**
     * 翻页参数名
     */
    private String pageNoParamName = DEFAULT_PAGE_NO_PARAM_NAME;

    private Integer pageSize = 10;

    private Integer totalCount;

    private List<T> data;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getPageNoParamName() {
        return pageNoParamName;
    }

    public void setPageNoParamName(String pageNoParamName) {
        this.pageNoParamName = pageNoParamName;
    }
}
