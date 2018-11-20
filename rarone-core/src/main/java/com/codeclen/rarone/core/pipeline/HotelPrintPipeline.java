package com.codeclen.rarone.core.pipeline;

import com.codeclen.rarone.core.instance.Hotel;

import java.util.Collection;

/**
 * @author lin
 * @since 2018/11/16.
 */
public class HotelPrintPipeline implements Pipeline<Hotel> {

    @Override
    public void process(Hotel a) {
        System.out.println(a.toString());
    }

    @Override
    public void process(Collection<Hotel> collection) {
        collection.forEach(a -> process(a));
    }
}
