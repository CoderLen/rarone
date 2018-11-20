package com.codeclen.rarone.core.download;

import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.Request;

/**
 * @author lin
 * @since 2018/11/14.
 */
public interface Downloader {

    /**
     * 下载页面数据
     * @param request
     * @return
     */
    public Page download(Request request);


}
