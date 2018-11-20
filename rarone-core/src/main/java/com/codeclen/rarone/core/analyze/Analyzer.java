package com.codeclen.rarone.core.analyze;

import com.codeclen.rarone.core.Page;

/**
 * @author lin
 * @since 2018/11/14.
 */
public interface Analyzer<T> {

    /**
     * 分析页面数据
     * @param page
     * @return
     */
    public T analyze(Page page);
}
