package com.codeclen.rarone.core.instance;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author lin
 * @since 2018/11/19.
 */
public class TesseractTest {

    @Test
    public void test(){
        ITesseract instance = new Tesseract();
        URL url = ClassLoader.getSystemResource("tessdata");
        String path = url.getPath().substring(1);
        instance.setDatapath(path);
        // 默认是英文（识别字母和数字），如果要识别中文(数字 + 中文），需要制定语言包
//        instance.setLanguage("chi_sim");
        try{
            BufferedImage image = ImageIO.read(new URL("https://images.bthhotels.com/PriceImg/Images/20181119/38f493a66c7b4161a8178db951f6e073.png"));
            String result1 = instance.doOCR(image);
            System.out.println(result1);
            System.out.println("width: " + image.getWidth() + ", Height: " + image.getHeight());
            int x = 403;
            int y = 6;
            Rectangle rectangle = new Rectangle(x, y, 12, 25);
            String result2 = instance.doOCR(image, rectangle);
            System.out.println(result2);
        }catch(TesseractException e){
            System.out.println(e.getMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
