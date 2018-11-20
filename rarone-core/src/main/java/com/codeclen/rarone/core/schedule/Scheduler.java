package com.codeclen.rarone.core.schedule;

import com.codeclen.rarone.core.Request;

/**
 * @author lin
 * @since 2018/11/14.
 */
public interface Scheduler {

    public void pull(Request request);

    public void poll();
}
