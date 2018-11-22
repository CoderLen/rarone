package com.codeclen.rarone.core.instance.bthhotel.pipeline;

import com.alibaba.fastjson.JSON;
import com.codeclen.rarone.core.instance.Hotel;
import com.codeclen.rarone.core.instance.bthhotel.extract.BthRoomExtractor;
import com.codeclen.rarone.core.pipeline.Pipeline;
import com.codeclen.rarone.core.util.ExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.*;

/**
 * @author lin
 * @since 2018/11/21.
 */
@Slf4j
public class BthHotelPipeline implements Pipeline<Hotel> {

    private static final String HOTEL_INFO_URL = "http://www.bthhotels.com/hotel/%s";
    private static final List<String> header = Arrays.asList("Id", "City", "Name", "First Brand", "Second Brand"
            , "Level", "Phone", "Address", "Coordinate", "Describe", "Policy", "Facilities");
    private BthRoomExtractor bthRoomExtractor = new BthRoomExtractor();
    private ExcelHelper excelHelper = new ExcelHelper();
    private List<List> datalist = new LinkedList<>();
    private XSSFWorkbook workbook;
    private String city;

    public BthHotelPipeline(XSSFWorkbook workbook, String city) {
        this.city = city;
        this.workbook = workbook;
        this.datalist.add(header);
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public void process(Hotel a) {
        List data = convertMap(a);
        this.datalist.add(data);
//        String url = String.format(HOTEL_INFO_URL, a.getHid());
//        Page<Room> rooms = bthRoomExtractor.run(url);
        log.info(JSON.toJSONString(a));
//        rooms.getData().forEach(room -> System.out.println(JSON.toJSONString(room)));
//        log.info("------------------------");
    }

    public List convertMap(Hotel hotel) {
        List list = new LinkedList();
        list.add(hotel.getHid());
        list.add(hotel.getCity());
        list.add(hotel.getName());
        list.add(hotel.getFirstBrand());
        list.add(hotel.getSecondBrand());
        list.add(hotel.getLevel());
        list.add(hotel.getPhone());
        list.add(hotel.getAddress());
        list.add(hotel.getCoordinate() != null ? hotel.getCoordinate().toString() : "");
        list.add(hotel.getDescribe());
        list.add(hotel.getPolicy());
        list.add(JSON.toJSONString(hotel.getFacilities()));
        return list;
    }

    @Override
    public void process(Collection<Hotel> collection) {
        collection.forEach(a -> process(a));
        Map<String, List<List>> listMap = new LinkedHashMap<>();
        listMap.put("data", this.datalist);
        try {
            excelHelper.appendExcelFile(this.workbook, listMap);
            this.datalist.clear();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
