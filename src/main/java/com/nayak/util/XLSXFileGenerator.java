package com.nayak.util;

import com.nayak.model.XpathColumnNameModel;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XLSXFileGenerator {

    private static final  Logger log= LoggerFactory.getLogger(XLSXFileGenerator.class);

    private XLSXFileGenerator() {
    }

    public static void generateXLSX(File xmlFile, File xlsxFile, boolean arrayedColumnName) {

        List<XpathColumnNameModel> xpathColumnNameModelList = XmlToColumnNameMapper.xmlToXpath(xmlFile);

        generate(xpathColumnNameModelList, xlsxFile, arrayedColumnName, "");
    }

    public static void generateXLSX(String xmlString, File xlsxFile, boolean arrayedColumnName) {

        List<XpathColumnNameModel> xpathColumnNameModelList = XmlToColumnNameMapper.xmlToXpath(xmlString);

        generate(xpathColumnNameModelList, xlsxFile, arrayedColumnName, "");

    }

    public static void generateXLSXForTestingSOAP(File requestXML, File responseXML, File xlsxFile, boolean arrayedColumnName, String responsePrefix) {
        List<XpathColumnNameModel> xpathColumnNameModelList = new ArrayList<>();

        xpathColumnNameModelList.addAll(XmlToColumnNameMapper.xmlToXpath(requestXML));
        xpathColumnNameModelList.addAll(XmlToColumnNameMapper.xmlToXpath(responseXML, responsePrefix));

        generate(xpathColumnNameModelList, xlsxFile, arrayedColumnName, responsePrefix);

    }

    public static void generateXLSXForTestingSOAP(String requestXML, String responseXML, File xlsxFile, boolean arrayedColumnName, String responsePrefix) {
        List<XpathColumnNameModel> xpathColumnNameModelList = new ArrayList<>();

        xpathColumnNameModelList.addAll(XmlToColumnNameMapper.xmlToXpath(requestXML));
        xpathColumnNameModelList.addAll(XmlToColumnNameMapper.xmlToXpath(responseXML, responsePrefix));


        generate(xpathColumnNameModelList, xlsxFile, arrayedColumnName, responsePrefix);

    }

    private static void generate(List<XpathColumnNameModel> mapping, File xlsxFile, boolean arrayedColumnName, String prefix) {

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("XML");
            sheet.setZoom(80);

            int rowNum = 0;
            XSSFRow row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (XpathColumnNameModel xpathColumnNameModel : mapping) {

                XSSFCell cell = row.createCell(colNum++);

                if(prefix.length()>0
                        && ( xpathColumnNameModel.getExcelColumn().startsWith(prefix)
                               || xpathColumnNameModel.getExcelColumNumberedArray().startsWith(prefix)) ) {

                    setCellColorAndFontColor(cell, IndexedColors.YELLOW, IndexedColors.BLACK);
                }
                else{
                    setCellColorAndFontColor(cell, IndexedColors.GREY_50_PERCENT, IndexedColors.WHITE);
                }

                if (arrayedColumnName) {
                    cell.setCellValue(xpathColumnNameModel.getExcelColumNumberedArray());
                } else {
                    cell.setCellValue(xpathColumnNameModel.getExcelColumn());
                }

            }
            for (int i = 0; i <= colNum; i++) {
                sheet.autoSizeColumn(i);
            }

            xlsxFile.delete();
            xlsxFile.getParentFile().mkdirs();
            xlsxFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(xlsxFile, false);

            workbook.write(outputStream);


        } catch (IOException e) {
            log.error("ERROR With XLSX Writing/Creating Process");
        }

    }

    public static void setCellColorAndFontColor(XSSFCell cell, IndexedColors foreGroundColor, IndexedColors fontColor) {
        XSSFWorkbook wb = cell.getRow().getSheet().getWorkbook();
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(fontColor.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(foreGroundColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }
}
