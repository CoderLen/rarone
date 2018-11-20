package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractHotelExtractor;
import com.codeclen.rarone.core.instance.Room;
import com.codeclen.rarone.core.instance.RoomRate;
import com.steadystate.css.parser.CSSOMParser;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lin
 * @since 2018/11/16.
 */
public class BthRoomExtractor extends AbstractHotelExtractor {

    private static final String HOTEL_ROOM_LIST_URL = "http://www.bthhotels.com/HotelAct/HotelRomList";

    private BthHotelDetailExtractor hotelDetailExtractor = new BthHotelDetailExtractor();
    {
        this.responseType = ResponseType.HTML;
    }

    private static Pattern pattern = Pattern.compile("[0-9]+");

    public static Map<String, String> classValMap = new HashMap<>();

    static {
        try{
            HttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://www.bthhotels.com/hotel/020063");
            HttpResponse response = client.execute(httpGet);
            String resStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            Document doc = Jsoup.parse(resStr);
            Elements elements = doc.head().select("style");
            InputSource source = new InputSource(new StringReader(elements.toString()));
            CSSOMParser parser = new CSSOMParser();
            CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
            CSSRuleList rules = sheet.getCssRules();
            ITesseract instance = new Tesseract();
            URL url = ClassLoader.getSystemResource("tessdata");
            String path = url.getPath().substring(1);
            instance.setDatapath(path);
            Map<String, BufferedImage> clazzValMap = new LinkedHashMap<>();
            for (int i = 0; i < rules.getLength(); i++) {
                final CSSRule rule = rules.item(i);
                //获取样式内容
                CSSStyleRule styleRule = (CSSStyleRule) rule;
                String[] selectors = styleRule.getSelectorText().split(",");
                if(selectors.length > 1){
                    String image = styleRule.getStyle().getPropertyValue("background-image");
                    if(StringUtils.isNotEmpty(image)){
                        String imageURL = image.replace("url(","").replace(")","");
                        BufferedImage img = ImageIO.read(new URL(imageURL));
                        for(String selector : selectors){
                            clazzValMap.put(selector, img);
                        }
                    }
                }else {
                    String selector = styleRule.getSelectorText();
                    if(clazzValMap.containsKey(selector)){
                        BufferedImage img = clazzValMap.get(selector);
                        String position = styleRule.getStyle().getPropertyValue("background-position");
                        String[] xy = position.split("\\s+");
                        if(xy.length == 2){
                            Integer x = 0-Integer.parseInt(xy[0].replace("px",""));
                            Integer y = 0-Integer.parseInt(xy[1].replace("px",""));
                            Rectangle rectangle = new Rectangle(x, y, 12, 25);
                            long s = System.currentTimeMillis();
                            try {
                                String text = instance.doOCR(img, rectangle).replaceAll("\\n|\\n\r","");
                                classValMap.put(selector.replace(".",""), text);
                            } catch (TesseractException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public Page<Room> extract(String html) {
        Page<Room> page = new Page<>();
        List<Room> rooms = new LinkedList<>();
        page.setData(rooms);
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".list_room_tj_row");
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
            List<RoomRate> roomRates = extractRoomRates(element);
            room.setRoomRates(roomRates);
            rooms.add(room);
        });
        return page;
    }

    public List<RoomRate> extractRoomRates(Element roomEle){
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
                if(classValMap.containsKey(className)){
                    priceBuf.append(classValMap.get(className));
                }else {
                    System.out.println("Can not found class val: " + className);
                }
            });
            System.out.println(priceBuf.toString());
        });

        return roomRates;
    }



}
