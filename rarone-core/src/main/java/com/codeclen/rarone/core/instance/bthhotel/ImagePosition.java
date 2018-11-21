package com.codeclen.rarone.core.instance.bthhotel;

import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author lin
 * @since 2018/11/21.
 */
@Data
public class ImagePosition {

    /**
     * 图片BufferedImage对象
     */
    private BufferedImage bufferedImage;

    /**
     * 图片中位置信息
     */
    private Rectangle rectangle;

    /**
     * 对应classs属性
     */
    private String seletor;




}
