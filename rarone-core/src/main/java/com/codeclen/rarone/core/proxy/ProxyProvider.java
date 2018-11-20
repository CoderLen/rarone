package com.codeclen.rarone.core.proxy;

import com.codeclen.rarone.core.Proxy;

/**
 * @author lin
 * @since 2018/11/14.
 */
public interface ProxyProvider {

    /**
     * 获取代理
     * @return
     */
    public Proxy getProxy();

    /**
     * 归还代理
     * @param proxy
     */
    public void releaseProxy(Proxy proxy);

}
