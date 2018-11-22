package com.codeclen.rarone.core.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lin on 2016/8/9.
 */
public class ExcelHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelHelper.class);
    final int maxColumnWidth = 255 * 256; // The maximum column width
    private Map<String, Workbook> tempWorkbook = new HashMap<String, Workbook>();

    public InputStream getFileInputStream(String filename) throws IOException {
        InputStream in = new FileInputStream(filename);
        return in;
    }

    /**
     * 资源关闭
     *
     * @param tempFilePath 模板文件路径
     * @param os           输出流
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void writeAndClose(String tempFilePath, OutputStream os) throws IOException {
        if (getTempWorkbook(tempFilePath) != null) {
            getTempWorkbook(tempFilePath).write(os);
            tempWorkbook.remove(tempFilePath);
        }
        if (getFileInputStream(tempFilePath) != null) {
            getFileInputStream(tempFilePath).close();
        }
    }

    /**
     * Excel单元格输出
     *
     * @param sheet
     * @param row       行
     * @param cell      列
     * @param value     值
     * @param cellStyle 样式
     */
    public void setValue(Sheet sheet, int row, int cell, Object value, CellStyle cellStyle) {
        Row rowIn = sheet.getRow(row);
        if (rowIn == null) {
            rowIn = sheet.createRow(row);
        }
        Cell cellIn = rowIn.getCell(cell);
        if (cellIn == null) {
            cellIn = rowIn.createCell(cell);
        }
        if (cellStyle != null) {
            //修复产生多超过4000 cellStyle 异常
            //CellStyle newStyle = wb.createCellStyle();
            //newStyle.cloneStyleFrom(cellStyle);
            cellIn.setCellStyle(cellStyle);
        }
        //对时间格式进行单独处理
        if (value == null) {
            cellIn.setCellValue("");
        } else {
            if (isCellDateFormatted(cellStyle)) {
                cellIn.setCellValue((Date) value);
            } else {
                cellIn.setCellValue(new XSSFRichTextString(value.toString()));
            }
        }
    }

    /**
     * Excel单元格输出
     *
     * @param sheet
     * @param column    行
     * @param row       列
     * @param value     值
     * @param cellStyle 样式
     */
    public void setColumn(Sheet sheet, int column, int row, Object value, CellStyle cellStyle) {
        Row rowIn = sheet.getRow(row);
        if (rowIn == null) {
            rowIn = sheet.createRow(row);
        }
        Cell cellIn = rowIn.getCell(column);
        if (cellIn == null) {
            cellIn = rowIn.createCell(column);
        }
        if (cellStyle != null) {
            //修复产生多超过4000 cellStyle 异常
            //CellStyle newStyle = wb.createCellStyle();
            //newStyle.cloneStyleFrom(cellStyle);
            cellIn.setCellStyle(cellStyle);
        }
        //对时间格式进行单独处理
        if (value == null) {
            cellIn.setCellValue("");
        } else {
            if (isCellDateFormatted(cellStyle)) {
                cellIn.setCellValue((Date) value);
            } else {
                cellIn.setCellValue(new XSSFRichTextString(value.toString()));
            }
        }
    }

    /**
     * 获得输入工作区
     *
     * @param tempFilePath
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Workbook getTempWorkbook(String tempFilePath) throws IOException {
        if (!tempWorkbook.containsKey(tempFilePath)) {
            if (tempFilePath.endsWith(".xlsx")) {
                tempWorkbook.put(tempFilePath, new XSSFWorkbook(getFileInputStream(tempFilePath)));
            } else if (tempFilePath.endsWith(".xls")) {
                tempWorkbook.put(tempFilePath, new HSSFWorkbook(getFileInputStream(tempFilePath)));
            }
        }
        return tempWorkbook.get(tempFilePath);
    }

    /**
     * 向Excel中输入相同title的多条数据
     *
     * @param tempFilePath excel模板文件路径
     * @param dataList     填充的数据
     * @param sheetName    填充的excel sheet的名字开始
     * @throws IOException
     */
    public synchronized void writeListData2NewModel(String tempFilePath, List<List<Object>> dataList, String sheetName) throws IOException {
        //按模板为写入板
        Workbook temWorkbook = getTempWorkbook(tempFilePath);
        //获取数据填充开始行
        int startCell = 2;

        //数据填充的sheet
        Sheet wsheet = temWorkbook.getSheet(sheetName);
        LOG.info(wsheet.toString());
        CellStyle titleStyle = temWorkbook.createCellStyle();     //在工作薄的基础上建立一个样式
        titleStyle.setBorderBottom(BorderStyle.THIN);    //设置边框样式
        titleStyle.setBorderLeft(BorderStyle.THIN);     //左边框
        titleStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setBorderRight(BorderStyle.THIN);    //右边框
        titleStyle.setBorderTop(BorderStyle.THIN);    //顶边框
        titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());    //
        if (dataList != null && dataList.size() > 0) {
            for (List<Object> datas : dataList) {
                int index = 0;
                for (Object value : datas) {
                    setValue(wsheet, startCell, index, value, titleStyle);
                    index++;
                    //写入数据
                }
                startCell++;
            }
        }
    }

    /**
     * 向Excel中输入相同title的多条数据
     *
     * @param tempFilePath excel模板文件路径
     * @param dataList     填充的数据
     * @param sheetName    填充的excel sheet的名字开始
     * @throws IOException
     */
    public synchronized void writeListData2Column(String tempFilePath, List<List<Object>> dataList, String sheetName) throws IOException {
        Workbook temWorkbook = getTempWorkbook(tempFilePath);
        //获取数据填充开始的列
        int column = 2;

        //数据填充的sheet
        Sheet wsheet = temWorkbook.getSheet(sheetName);
        LOG.info(wsheet.toString());
        CellStyle titleStyle = temWorkbook.createCellStyle();     //在工作薄的基础上建立一个样式
        titleStyle.setBorderBottom(BorderStyle.THIN);    //设置边框样式
        titleStyle.setBorderLeft(BorderStyle.THIN);     //左边框
        titleStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setBorderRight(BorderStyle.THIN);    //右边框
        titleStyle.setBorderTop(BorderStyle.THIN);    //顶边框
        titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());    //
        if (dataList != null && dataList.size() > 0) {
            for (List<Object> datas : dataList) {
                int index = 0;
                for (Object value : datas) {
                    setColumn(wsheet, column, index, value, titleStyle);
                    index++;
                    //写入数据
                }
                column++;
            }
        }
    }

    public synchronized void writeListData(String tempFilePath, List<List<Object>> dataList, String keywords, int skipRows, int sheet, String sheetName) throws IOException {
        //按模板为写入板
        Workbook temWorkbook = getTempWorkbook(tempFilePath);
        //获取数据填充开始行
        int startCell = skipRows;

        //数据填充的sheet
        Sheet wsheet = temWorkbook.getSheetAt(sheet);
        if (wsheet == null) {
            wsheet = temWorkbook.createSheet();
            int num = sheet - 1;
            Sheet temp = temWorkbook.getSheetAt(num);
            for (int i = 0; i < 2; i++) {
                Row oldRow = temp.getRow(i);
                Row newRow = temp.createRow(i);
                copyRow(temWorkbook, oldRow, newRow, false);
            }
            wsheet = temWorkbook.getSheetAt(sheet);
        } else {
            temWorkbook.setSheetName(sheet, sheetName);
        }
        if (keywords != null) {
            setValue(wsheet, 0, 1, keywords, null);
        }
        CellStyle titleStyle = temWorkbook.createCellStyle();     //在工作薄的基础上建立一个样式
        titleStyle.setBorderBottom(BorderStyle.THIN);    //设置边框样式
        titleStyle.setBorderLeft(BorderStyle.THIN);     //左边框
        titleStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setBorderRight(BorderStyle.THIN);    //右边框
        titleStyle.setBorderTop(BorderStyle.THIN);    //顶边框
        titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());    //
        if (dataList != null && dataList.size() > 0) {
            for (List<Object> datas : dataList) {
                int index = 0;
                for (Object value : datas) {
                    setValue(wsheet, startCell, index, value, titleStyle);
                    index++;
                    //写入数据
                }
                startCell++;
            }
        }
    }

    /**
     * 向Excel中输入相同title的多条数据
     *
     * @param tempFilePath excel模板文件路径
     * @param dataList     填充的数据
     * @param sheet        填充的excel sheet,从0开始
     * @throws IOException
     */
    public synchronized void writeListData(String tempFilePath, List<List<Object>> dataList, String keywords, int sheet, String sheetName) throws IOException {
        writeListData(tempFilePath, dataList, keywords, 3, sheet, sheetName);
    }

    /**
     * 行复制功能
     *
     * @param fromRow
     * @param toRow
     */
    public void copyRow(Workbook wb, Row fromRow, Row toRow, boolean copyValueFlag) {
        for (Iterator cellIt = fromRow.cellIterator(); cellIt.hasNext(); ) {
            Cell tmpCell = (Cell) cellIt.next();
            Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb, tmpCell, newCell, copyValueFlag);
        }
    }

    /**
     * 复制一个单元格样式到目的单元格样式
     *
     * @param fromStyle
     * @param toStyle
     */
    public void copyCellStyle(CellStyle fromStyle,
                              CellStyle toStyle) {
        toStyle.cloneStyleFrom(fromStyle);
        toStyle.setWrapText(fromStyle.getWrapText());
    }

    /**
     * 复制单元格
     *
     * @param srcCell
     * @param distCell
     * @param copyValueFlag true则连同cell的内容一起复制
     */
    public void copyCell(Workbook wb, Cell srcCell, Cell distCell,
                         boolean copyValueFlag) {
        CellStyle newstyle = wb.createCellStyle();
        copyCellStyle(srcCell.getCellStyle(), newstyle);
        //样式
        distCell.setCellStyle(newstyle);
        //评论
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }
        // 不同数据类型处理
        CellType srcCellType = srcCell.getCellType();
        distCell.setCellType(srcCellType);
        if (copyValueFlag) {
            if (srcCellType == CellType.NUMERIC) {
                if (isCellDateFormatted(srcCell.getCellStyle())) {
                    distCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    distCell.setCellValue(srcCell.getNumericCellValue());
                }
            } else if (srcCellType == CellType.STRING) {
                distCell.setCellValue(srcCell.getRichStringCellValue());
            } else if (srcCellType == CellType.BLANK) {
                // nothing21
            } else if (srcCellType == CellType.BOOLEAN) {
                distCell.setCellValue(srcCell.getBooleanCellValue());
            } else if (srcCellType == CellType.ERROR) {
                distCell.setCellErrorValue(srcCell.getErrorCellValue());
            } else if (srcCellType == CellType.FORMULA) {
                distCell.setCellFormula(srcCell.getCellFormula());
            } else { // nothing29
            }
        }
    }


    /**
     * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
     *
     * @param file       读取数据的源Excel
     * @param ignoreRows 读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
     * @return 读出的Excel中数据的内容
     * @throws FileNotFoundException
     * @throws IOException
     */

    public String[][] getExcelData(File file, String sheetName, int ignoreRows) throws IOException {
        List<String[]> result = new ArrayList<String[]>();
        Workbook wb = getTempWorkbook(file.getPath());
        Sheet st = wb.getSheet(sheetName);
        if (st == null) {
            throw new IllegalArgumentException("没找到对应的Sheet,请检查，SheetName:" + sheetName);
        }
        return getExcelData(st, ignoreRows);
    }


    /**
     * 读取Excel的内容，第一维数组存储的是一行中格列的值，二维数组存储的是多少个行
     *
     * @param inputStream 读取数据的源Excel
     * @param ignoreRows  读取数据忽略的行数，比喻行头不需要读入 忽略的行数为1
     * @return 读出的Excel中数据的内容
     * @throws IOException
     */

    public String[][] getExcelData(InputStream inputStream, int ignoreRows) throws Exception {

        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet st = workbook.getSheetAt(0);
            if (st == null) {
                throw new IllegalArgumentException("没找到对应的Sheet,请检查!");
            }
            return getExcelData(st, ignoreRows);
        }catch (IOException e){
            throw new Exception("Open excel file error.");
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String[][] getExcelData(Sheet st, int ignoreRows) {
        List<String[]> result = new ArrayList<String[]>();
        int rowSize = 0;
        Cell cell = null;
        // 第一行为标题，不取
        for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
            Row row = st.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            int tempRowSize = row.getLastCellNum() + 1;
            if (tempRowSize > rowSize) {
                rowSize = tempRowSize;
            }

            String[] values = new String[rowSize];
            Arrays.fill(values, "");
            boolean hasValue = false;
            for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
                String value = "";
                cell = row.getCell(columnIndex);
                if (cell != null) {
                    // 注意：一定要设成这个，否则可能会出现乱码
                    CellType cellType = cell.getCellType();
                    if (cellType == CellType.STRING) {
                        value = cell.getStringCellValue();
                    } else if (cellType == CellType.NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            if (date != null) {
                                value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                            } else {
                                value = "";
                            }
                        } else {
                            value = new DecimalFormat("0").format(cell.getNumericCellValue());
                        }
                    } else if (cellType == CellType.FORMULA) {
                        // 导入时如果为公式生成的数据则无值
                        if (!cell.getStringCellValue().equals("")) {
                            value = cell.getStringCellValue();
                        } else {
                            value = cell.getNumericCellValue() + "";
                        }
                    } else if (cellType == CellType.ERROR) {
                        value = "";
                    } else if (cellType == CellType.BOOLEAN) {
                        value = (cell.getBooleanCellValue() == true ? "Y" : "N");
                    } else {
                        value = "";
                    }
                }
                if (columnIndex == 0 && value.trim().equals("")) {
                    break;
                }
                values[columnIndex] = rightTrim(value);
                hasValue = true;
            }
            if (hasValue) {
                result.add(values);
            }
        }

        String[][] returnArray = new String[result.size()][rowSize];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = result.get(i);
        }
        return returnArray;
    }

    /**
     * 去掉字符串右边的空格
     *
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */

    public String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20) {
                break;
            }
            length--;
        }
        return str.substring(0, length);
    }

    public synchronized void createExcelFile(String path, Map<String, List<List>> dataMap) {
        Workbook workbook = new XSSFWorkbook();
        for (Map.Entry<String, List<List>> entry : dataMap.entrySet()) {
            createSheet(workbook, entry.getKey(), entry.getValue(), false);
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                LOG.info("create file:{}", file.getAbsolutePath());
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Workbook createWorkbook(Workbook workbook, Map<String, List<List>> dataMap, String desc) {
        for (Map.Entry<String, List<List>> entry : dataMap.entrySet()) {
            createSheetByCol(workbook, entry.getKey(), entry.getValue(), desc);
        }
        return workbook;
    }

    public synchronized Workbook createWorkbook2(Workbook workbook, Map<String, List<List>> dataMap) {
        for (Map.Entry<String, List<List>> entry : dataMap.entrySet()) {
            createSheet(workbook, entry.getKey(), entry.getValue(), true);
        }
        return workbook;
    }

    public synchronized Workbook createWorkbook(Workbook workbook, Map<String, List<List>> dataMap) {
        for (Map.Entry<String, List<List>> entry : dataMap.entrySet()) {
            createSheetByCol(workbook, entry.getKey(), entry.getValue(), null);
        }
        return workbook;
    }

    public synchronized void createDefinedRow(Workbook workbook, String sheetName, String value) {
        Sheet sheet = workbook.getSheet(sheetName);
        int rowNum = 0;
        if (sheet != null) {
            rowNum = sheet.getLastRowNum() + 2;
        } else {
            sheet = workbook.createSheet(sheetName);
        }
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.createCell(0);
        CellStyle cellStyle = workbook.createCellStyle();     //在工作薄的基础上建立一个样式
        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());
        cellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }

    public synchronized void appendExcelFile(String path, Map<String, List<List>> dataMap) throws IOException {
        File infile = new File(path);
        if (!infile.getParentFile().exists()) {
            infile.getParentFile().mkdirs();
        }
        if(!infile.exists()){
            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream outputStream = new FileOutputStream(infile);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
        }
        FileInputStream fs = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(fs);
        CellStyle cellStyle = createCellStyle(workbook);
        for (Map.Entry<String, List<List>> entry : dataMap.entrySet()) {
            Integer sheetIndex = workbook.getSheetIndex(entry.getKey());
            XSSFSheet sheet;
            if (sheetIndex == -1) {
                sheet = workbook.createSheet(entry.getKey());
            } else {
                sheet = workbook.getSheetAt(sheetIndex);
            }
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum == 0) {
                Row headRow = sheet.createRow(0);
                List<List> datalist = entry.getValue();
                List<Object> headRowData = datalist.get(0);
                for (int i = 0; i < headRowData.size(); i++) {
                    Object data = headRowData.get(i);
                    if (data == null) {
                        headRow.createCell(i, CellType.BLANK);
                    } else {
                        CellStyle titleStyle = createHeadStyle(workbook, true);
                        Cell cell = headRow.createCell(i, CellType.STRING);
                        cell.setCellValue(data.toString());
                        cell.setCellStyle(titleStyle);
                    }
                }
                lastRowNum++;
            }
            List<List> datalist = entry.getValue();
            for (int rowNum = 1; rowNum < datalist.size(); rowNum++) {
                XSSFRow row = sheet.createRow(lastRowNum);
                List<Object> rowData = datalist.get(rowNum);
                for (int i = 0; i < rowData.size(); i++) {
                    Object data = rowData.get(i);
                    if (data == null) {
                        row.createCell(i, CellType.BLANK);
                    } else if (data instanceof Integer) {
                        Cell cell = row.createCell(i, CellType.NUMERIC);
                        cell.setCellValue((Integer) data);
                        cell.setCellStyle(cellStyle);
                    } else if (data instanceof Double) {
                        Cell cell = row.createCell(i, CellType.NUMERIC);
                        cell.setCellValue((Double) data);
                        cell.setCellStyle(cellStyle);
                    } else {
                        Cell cell = row.createCell(i, CellType.STRING);
                        cell.setCellValue(data.toString());
                        cell.setCellStyle(cellStyle);
                    }
                }
                lastRowNum = lastRowNum + 1;
            }
            if (datalist != null && datalist.size() > 0) {
                List<Object> headData = datalist.get(0);
                for (int k = 0; k < headData.size(); k++) {
                    sheet.autoSizeColumn((short) k);
                }
            }
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void appendExcelFile(XSSFWorkbook workbook, Map<String, List<List>> dataMap) throws IOException {
        for (Map.Entry<String, List<List>> entry : dataMap.entrySet()) {
            createSheet(workbook, entry.getKey(), entry.getValue(), false);
        }
    }

    public CellStyle createHeadStyle(Workbook workbook, boolean bgColor) {
        CellStyle style = workbook.createCellStyle();     //在工作薄的基础上建立一个样式
        style.setBorderBottom(BorderStyle.THIN);    //设置边框样式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderLeft(BorderStyle.THIN);     //左边框
        style.setBorderRight(BorderStyle.THIN);    //右边框
        if (bgColor) {
            style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex()); //背景色
        }
        style.setBorderTop(BorderStyle.THIN);    //顶边框
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex());    //
        return style;
    }

    public CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();     //在工作薄的基础上建立一个样式
        style.setBorderBottom(BorderStyle.THIN);    //设置边框样式
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setBorderLeft(BorderStyle.THIN);     //左边框
        style.setBorderRight(BorderStyle.THIN);    //右边框
        style.setBorderTop(BorderStyle.THIN);    //顶边框
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex());    //
        return style;
    }

    public CellStyle createDescStyle(Workbook workbook, boolean bgColor) {
        CellStyle style = workbook.createCellStyle();     //在工作薄的基础上建立一个样式
        style.setAlignment(HorizontalAlignment.LEFT);
        if (bgColor) {
            style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex()); //背景色
        }
        return style;
    }

    public void createSheet(Workbook workbook, String sheetName, List<List> datalist, Boolean isCellCenter) {
        Sheet sheet = workbook.getSheet(sheetName);
        int index = 0;
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        } else {
            index = sheet.getLastRowNum() + 1;
        }
        CellStyle titleStyle = createHeadStyle(workbook, true);
        CellStyle cellStyle;
        if (isCellCenter) {
            cellStyle = createHeadStyle(workbook, false);
        } else {
            cellStyle = createCellStyle(workbook);
        }
        Row headRow = sheet.createRow(index);
        List<Object> headRowData = datalist.get(0);
        for (int i = 0; i < headRowData.size(); i++) {
            Object data = headRowData.get(i);
            if (data == null) {
                Cell cell = headRow.createCell(i, CellType.BLANK);
                cell.setCellStyle(titleStyle);
            } else {
                Cell cell = headRow.createCell(i, CellType.STRING);
                cell.setCellValue(data.toString());
                cell.setCellStyle(titleStyle);
            }
        }
        for (int rowNum = 1; rowNum < datalist.size(); rowNum++) {
            Row row = sheet.createRow(rowNum + index);
            List<Object> rowData = datalist.get(rowNum);
            for (int i = 0; i < rowData.size(); i++) {
                Object data = rowData.get(i);
                if (data == null) {
                    Cell cell = row.createCell(i, CellType.BLANK);
                    cell.setCellStyle(cellStyle);
                } else if (data instanceof Integer) {
                    Cell cell = row.createCell(i, CellType.NUMERIC);
                    cell.setCellValue((Integer) data);
                    cell.setCellStyle(cellStyle);
                } else if (data instanceof Double) {
                    Cell cell = row.createCell(i, CellType.NUMERIC);
                    cell.setCellValue((Double) data);
                    cell.setCellStyle(cellStyle);
                } else {
                    Cell cell = row.createCell(i, CellType.STRING);
                    cell.setCellValue(data.toString());
                    cell.setCellStyle(cellStyle);
                }
            }
        }
        List<Object> headData = datalist.get(0);
        for (int k = 0; k < headData.size(); k++) {
            sheet.autoSizeColumn((short) k);
        }
    }

    public void createSheetByCol(Workbook workbook, String sheetName, List<List> datalist, String desc) {
        Sheet sheet = workbook.getSheet(sheetName);
        CellStyle titleStyle = createHeadStyle(workbook, true);
        CellStyle descStyle = createDescStyle(workbook, true);
        int index = 0;
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        } else {
            index = sheet.getLastRowNum() + 1;
        }
        CellStyle cellStyle = workbook.createCellStyle();     //在工作薄的基础上建立一个样式
        cellStyle.setBorderBottom(BorderStyle.THIN);    //设置边框样式
        cellStyle.setBorderLeft(BorderStyle.THIN);     //左边框
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("0.00"));
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderRight(BorderStyle.THIN);    //右边框
        cellStyle.setBorderTop(BorderStyle.THIN);    //顶边框
        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());    //
        if (desc != null) {
            setColumn(sheet, 0, index, desc, descStyle);
            CellRangeAddress cra = new CellRangeAddress(index, index, 0, datalist.size() - 1);
            sheet.addMergedRegion(cra);
            index++;
        }
        int column = 0;
        if (datalist != null && datalist.size() > 0) {
            for (List<Object> datas : datalist) {
                for (Object value : datas) {
                    setColumn(sheet, column, index, value, cellStyle);
                    index++;
                    //写入数据
                }
                index = index - datas.size();
                column++;
            }
        }
        Row firstRow = sheet.getRow(index);
        for (int k = 0; k < datalist.size(); k++) {
            if (firstRow != null) {
                Cell firstCell = firstRow.getCell(k);
                if (firstCell != null) {
                    String val = firstCell.getStringCellValue();
                    int width = val.length() * 628;
                    if (width > maxColumnWidth) {
                        width = maxColumnWidth;
                    }
                    sheet.setColumnWidth(k, width);
                    firstCell.setCellStyle(titleStyle);
                }
            }
        }
    }

    /**
     * 根据表格样式判断是否为日期格式
     *
     * @param cellStyle
     * @return
     */
    public boolean isCellDateFormatted(CellStyle cellStyle) {
        if (cellStyle == null) {
            return false;
        }
        int i = cellStyle.getDataFormat();
        String f = cellStyle.getDataFormatString();

        return org.apache.poi.ss.usermodel.DateUtil.isADateFormat(i, f);
    }
}
