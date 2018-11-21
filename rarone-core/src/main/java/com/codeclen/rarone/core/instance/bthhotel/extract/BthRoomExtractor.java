package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractHotelExtractor;
import com.codeclen.rarone.core.instance.Room;
import com.codeclen.rarone.core.instance.RoomRate;
import com.codeclen.rarone.core.instance.bthhotel.ImagePosition;
import com.steadystate.css.parser.CSSOMParser;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lin
 * @since 2018/11/16.
 */
@Slf4j
public class BthRoomExtractor extends AbstractHotelExtractor {

    private static final String HOTEL_ROOM_LIST_URL = "http://www.bthhotels.com/HotelAct/HotelRomList";

    private BthHotelDetailExtractor hotelDetailExtractor = new BthHotelDetailExtractor();
    {
        this.responseType = ResponseType.ASYNHTML;
    }

    private static Pattern pattern = Pattern.compile("[0-9]+");


    private Map<String,ImagePosition> getPriceClazzMap(Document doc) throws IOException {
        Map<String, ImagePosition> classValMap = new HashMap<>();
        Elements elements = doc.head().select("style");
        InputSource source = new InputSource(new StringReader(elements.toString()));
        CSSOMParser parser = new CSSOMParser();
        CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
        CSSRuleList rules = sheet.getCssRules();

        Map<String, BufferedImage> clazzValMap = new HashMap<>(rules.getLength());
        for (int i = 0; i < rules.getLength(); i++) {
            final CSSRule rule = rules.item(i);
            //获取样式内容
            CSSStyleRule styleRule = (CSSStyleRule) rule;
            String image = styleRule.getStyle().getPropertyValue("background-image");
            if(StringUtils.isNotEmpty(image)){
                String imageURL = image.replace("url(","").replace(")","");
                BufferedImage img = ImageIO.read(new URL(imageURL));
//                FileUtils.copyInputStreamToFile(new URL(imageURL).openConnection().getInputStream(), new File("G:\\test\\images\\" + System.currentTimeMillis()+".png"));
                String[] selectors = styleRule.getSelectorText().split(",");
                for(String selector : selectors){
                    clazzValMap.put(selector.trim(), img);
                }
            }else {
                CSSStyleDeclaration cssStyleDeclaration = styleRule.getStyle();
                String selector = styleRule.getSelectorText();
                String position = cssStyleDeclaration.getPropertyValue("background-position");
                if(StringUtils.isNotEmpty(position) && clazzValMap.containsKey(selector)){
                    BufferedImage img = clazzValMap.get(selector);
                    String[] xy = position.split("\\s+");
                    if(xy.length == 2){
                        Integer x = 0-Integer.parseInt(xy[0].replace("px",""));
                        Integer y = 0-Integer.parseInt(xy[1].replace("px",""));
                        Rectangle rectangle = new Rectangle(x, y, 12, 25);
                        ImagePosition imagePosition = new ImagePosition();
                        imagePosition.setBufferedImage(img);
                        imagePosition.setRectangle(rectangle);
                        imagePosition.setSeletor(selector);
                        classValMap.put(selector.replace(".",""), imagePosition);
                    }else {
                        log.warn("illegal position: {}", position);
                    }
                }else {
                    log.warn("没找到对应的class: {}", selector);
                }
            }
        }
        return classValMap;
    }
    @Override
    public Page<Room> extract(String html) {
        Page<Room> page = new Page<>();
        List<Room> rooms = new LinkedList<>();
        page.setData(rooms);

        try {
            Document document = Jsoup.parse(html);
            //获取价格样式图片的样式
            Map<String, ImagePosition> priceMap = getPriceClazzMap(document);
            Elements elements = document.select("#roomTypeContanner > div > div.list_roomtype > div.list_room_tj_row");

            ITesseract instance = new Tesseract();
            URL url = ClassLoader.getSystemResource("tessdata");
            String path = url.getPath().substring(1);
            instance.setLanguage("num");
            instance.setDatapath(path);

            elements.forEach(element -> {
                Elements roomTypeVal = element.select("ul.list_room_w236 > dl.list_room_intro > dt:nth-child(1)");
                String roomType = roomTypeVal.text();
                Elements maxCheckIn = element.select("ul.list_room_w236 > dl > dd:nth-child(3)");
                String maxCheckInVal = maxCheckIn.text();
                Elements roomSizeEle = element.select("ul.list_room_w236 > dl > dd:nth-child(4)");
                String roomSize = roomSizeEle.text();
                Room room = new Room();
                room.setRoomSize(roomSize);
                room.setRoomType(roomType);
                Matcher matcher = pattern.matcher(maxCheckInVal);
                room.setMaxCheckin(matcher.find() ? Integer.parseInt(matcher.group()): 0);
                String image = element.select(".list_room_img").attr("src_img");
                room.setImage(image);
                List<RoomRate> roomRates = extractRoomRates(element, priceMap, instance);
                room.setRoomRates(roomRates);
                rooms.add(room);
            });
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    public List<RoomRate> extractRoomRates(Element roomEle, Map<String, ImagePosition> priceMap, ITesseract instance){
        List<RoomRate> roomRates = new LinkedList<>();
        Elements roomPriceEls = roomEle.select(".list_room_fixbox").select(".list_room_fix");
        roomPriceEls.forEach(element -> {
            RoomRate roomRate = new RoomRate();
            String priceType = element.select(".list_room_w154").text();
            roomRate.setRateName(priceType);
            Elements priceEls = element.select(".list_room_w345 > div.price_row > a > span > var");
            StringBuffer priceBuf = new StringBuffer();
            priceEls.forEach(e -> {
                String className = e.className();
                if(priceMap.containsKey(className)){
                    ImagePosition imagePosition = priceMap.get(className);
                    try {
                        String text = instance.doOCR(imagePosition.getBufferedImage(), imagePosition.getRectangle());
                        priceBuf.append(text.trim());
                    } catch (TesseractException e1) {
                        log.error(e1.getMessage(), e1);
                    }
                }else {
                    log.error("Can not found class val: " + className);
                }
            });
            if(priceBuf.length() > 0){
                try {
                    roomRate.setSellingRate(Double.parseDouble(priceBuf.toString()));
                }catch (Exception e){
                    log.error(e.getMessage(), e);
                }
            }
            roomRates.add(roomRate);
        });
        return roomRates;
    }



}
