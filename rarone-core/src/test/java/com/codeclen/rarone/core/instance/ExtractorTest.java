package com.codeclen.rarone.core.instance;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.City;
import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelCityExtractor;
import com.codeclen.rarone.core.instance.bestwehotel.extract.BestwehotelHotelExtractor;
import com.codeclen.rarone.core.pipeline.impl.BestwehotelHotelPipeline;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.steadystate.css.parser.CSSOMParser;
import lombok.NonNull;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lin
 * @since 2018/11/15.
 */
public class ExtractorTest {

    @Test
    public void action() throws Exception {
        BestwehotelHotelExtractor hotelExtractor = new BestwehotelHotelExtractor();
        BestwehotelCityExtractor cityExtractor = new BestwehotelCityExtractor();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("channelCode", "CA00003");
        Page<City> cityList = cityExtractor.run("http://hotel.bestwehotel.com/api/hotel/queryAllCites", paramsMap);
        ForkJoinPool forkJoinPool = new ForkJoinPool(5);
        forkJoinPool.submit(() -> {
            cityList.getData().parallelStream().forEach(city -> {
                System.out.println("----------- " + city.getCityName() + "|" + Thread.currentThread().getName() + " ------------");
                Map<String, Object> params = new HashMap<>();
                params.put("channelCode", "CA00046");
                params.put("page", 1);
                params.put("size", 10);
                params.put("cityCode", city.getCityCode());
                params.put("checkInDate", "2018-11-15");
                hotelExtractor.withUrl("http://hotel.bestwehotel.com/api/hotel/searchHotels")
                        .withParams(params).withPipeline(new BestwehotelHotelPipeline()).action();
            });
        });
        forkJoinPool.awaitTermination(1000, TimeUnit.SECONDS);

    }


    /**
     * 请求接口获取json信息
     *
     * @param url
     * @param params
     * @return
     */
    protected Document requestHtml(@NonNull String url, Map<String, Object> params) {
        HttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        if (params != null && !params.isEmpty()) {
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                postParameters.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
            try {
                request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            HttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();

            String resStr = EntityUtils.toString(responseEntity, "UTF-8");
            Document doc = Jsoup.parse(resStr);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            request.abort();
        }
        return null;
    }


    @Test
    public void testhtml() {
        Map<String, Object> params = new HashMap<>();
        params.put("cityCode", "0200");
        params.put("cityName", "guangzhou");
        params.put("beginDate", "2018/11/19");
        params.put("endDate", "2018/11/20");
        params.put("pageNo", "2");
        Document document = requestHtml("http://www.bthhotels.com/listasync", params);
        Elements elements = document.select(".list_boxline");
        elements.forEach(element -> {
            Elements introEle = element.select(".list_intro_txt");
            String hotelId = introEle.select(".list_intro_name_tj>a").attr("data-hotelcd");
            Elements nameEle = introEle.select(".list_intro_name_tj>a");
            Elements addressEle = introEle.select(".list_intro_address_tj>span");
            Elements facEles = introEle.select(".list_intro_icon>span");
            List<String> facList = new LinkedList<>();
            facEles.forEach(fac -> {
                String facVal = fac.attr("title");
                facList.add(facVal);
            });
            String location = element.attr("data-point");
            System.out.println(location);
            System.out.println(hotelId + ":" + nameEle.attr("title") + ":" + addressEle.attr("title") + ":" + JSON.toJSONString(facList));
        });
//        System.out.println(document);
    }


    @Test
    public void testHoteldatail() throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://www.bthhotels.com/hotel/020063");
        HttpResponse response = client.execute(httpGet);
        String resStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        Document doc = Jsoup.parse(resStr);
        Elements elements = doc.head().select("style");
//        String content = elements.toString().replaceAll("<style type=\"text/css\">", "")
//                .replaceAll("</style>", "").trim();
//        System.out.println(content);
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
                            System.out.println(String.format("Found: %s:%s, Costs %d ms.", selector, text, System.currentTimeMillis() - s));
                        } catch (TesseractException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        final Pattern pattern = Pattern.compile("[0-9]+");
        elements = doc.select("#roomTypeContanner > div > div.list_roomtype > div.list_room_tj_row");
        elements.forEach(element -> {
            Elements roomTypeVal = element.select("ul.list_room_w236 > dl > dd:nth-child(1)");
            String roomType = roomTypeVal.text();
            Elements maxCheckIn = element.select("ul.list_room_w236 > dl > dd:nth-child(3)");
            String maxCheckInVal = maxCheckIn.val();
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
        });

    }

    public List<RoomRate> extractRoomRates(Element roomEle){
        List<RoomRate> roomRates = new LinkedList<>();
        Elements roomPriceEls = roomEle.select(".list_room_fixbox").select(".list_room_fix");
        roomPriceEls.forEach(element -> {
            RoomRate roomRate = new RoomRate();
            String priceType = element.select(".list_room_w154").text();
            roomRate.setRateName(priceType);
            Elements priceEls = element.select("ul.list_room_w345 > div.price_row > a > span");
            priceEls.forEach(e -> {
                String className = e.className();

            });
        });

        return roomRates;
    }


    @Test
    public void testConnect() throws IOException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000);
        HtmlPage htmlPage = null;
        try {
            htmlPage = webClient.getPage("http://www.bthhotels.com/hotel/020063");
            webClient.waitForBackgroundJavaScript(2000);
            String htmlString = htmlPage.asXml();
            Document document = Jsoup.parse(htmlString);
            System.out.println(document);
        } finally {
            webClient.close();
        }
    }
}
