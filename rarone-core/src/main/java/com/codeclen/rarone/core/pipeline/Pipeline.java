package com.codeclen.rarone.core.pipeline;

import java.util.Collection;

/**
 * 数据处理管道
 * @author lin
 * @since 2018/11/15.
 */
public interface Pipeline<T> {

    /**
     * 单独处理一条数据
     * @param a
     */
    void process(T a);

    /**
     * 批处理
     * @param collection
     */
    void process(Collection<T> collection);
}
