package com.nayak.util;

import com.nayak.model.XPathModel;
import com.nayak.model.XpathColumnNameModel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * -> XML to XLSX header only
 * -> Single populated XML to XLSX
 * -> Multiple populated XML to XLSX
 */

public class XmlToXlsxGenerator {

    private XmlToXlsxGenerator() {
    }

    private static Map<String, String> xmlToHashMap(String firstXmlString, boolean headerOnly, String secondXmlString, String secondXmlStringPrefix, boolean columnNumberedArray) {

        Map<String, String> hashMap = new LinkedHashMap<>();

        String xmlString = firstXmlString.replace("></", ">?</");
        List<XpathColumnNameModel> columnName = XmlToColumnNameMapper.xmlToXpath(xmlString);
        List<XPathModel> xpathModelList = new XpathGen().xmlToXpath(xmlString);

        for (int i = 0; i < columnName.size(); i++) {
            hashMap.put("first" + i, columnName.get(i).getExcelColumn() + "|" + xpathModelList.get(i).getValue());
        }

        if (!secondXmlString.isEmpty()) {

            xmlString = secondXmlString.replace("></", ">?</");
            columnName = XmlToColumnNameMapper.xmlToXpath(xmlString, secondXmlStringPrefix);
            xpathModelList = new XpathGen().xmlToXpath(xmlString);

            for (int i = 0; i < columnName.size(); i++) {
                hashMap.put("second" + i, columnName.get(i).getExcelColumn() + "|" + xpathModelList.get(i).getValue());
            }
        }




        return hashMap;
    }

    public static File xlsxFileGenerator(String sheetName, String firstXmlString, boolean headerOnly, String secondXmlString, String secondXmlStringPrefix, boolean columnNumberedArray) {

        String fileExtension = "xlsx";

        return createXLSX(Arrays.asList(xmlToHashMap(firstXmlString, false, secondXmlString, secondXmlStringPrefix, false)), sheetName, fileExtension);

    }

    public static File xlsxFileGenerator(List<String> xmlStrings, boolean headerOnly, List<String> secondXmlString, List<String> secondXmlStringPrefix, boolean columnNumberedArray) {

        return null;
    }

    public static File xlsxFileGenerator(String sheetName, String xmlStrings, boolean headerOnly, boolean columnNumberedArray) {

        String fileExtension = "xlsx";

        return createXLSX(Arrays.asList(xmlToHashMap(xmlStrings, false, "", "", false)), sheetName, fileExtension);
    }

    public static File xlsxFileGenerator(String sheetName, List<String> xmlStrings, boolean headerOnly, boolean columnNumberedArray) {

        String fileExtension = "xlsx";

        List<Map<String, String>> xmlString = new ArrayList<>();

        Map<String, String> map;

        for (String xml : xmlStrings) {
            map = xmlToHashMap(xml, false, "", "", false);
            xmlString.add(map);
        }

        return createXLSX(xmlString, sheetName, fileExtension);
    }

    private static File createTempFile(String fileExtension) {
        String tempFolderPath = System.getProperty("java.io.tmpdir");
        File xlsxFile = new File(tempFolderPath + System.currentTimeMillis() + "." + fileExtension);
        xlsxFile.delete();
        xlsxFile.getParentFile().mkdirs();
        try {
            xlsxFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(xlsxFile.getAbsoluteFile());

        return xlsxFile;
    }

    public static boolean validateXMLSameSchema(List<String> xmlStrings) {
        boolean validationResult = false;

        if (xmlStrings.size() > 1) {
            boolean isUnique = false;
            isUnique = xmlStrings.stream()
                    .map(xml -> XmlToColumnNameMapper.xmlToXpath(xml.replace("></", ">?</")).size())
                    .distinct().count() == 1 ? true : false;

            Map<String, Long> varInList = xmlStrings.stream()
                    .map(xml -> XmlToColumnNameMapper.xmlToXpath(xml.replace("></", ">?</")))
                    .map(xml -> xml.stream().map(XpathColumnNameModel::getExcelColumn).collect(Collectors.toList()))
                    .map(xml -> xml.toString()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            isUnique = varInList.size() == 1 ? true : false;

            //TODO: add logic to check column name as well...Though i don't think it's needed

            if (isUnique) validationResult = true;
        } else validationResult = true;

        return validationResult;
    }

    private static File createXLSX(List<Map<String, String>> mapList, String sheetName, String fileExtension) {

        File xlsxFile = createTempFile(fileExtension);

        List<String> headerList = mapList.get(0).values().stream().map(head -> head.split("\\|")[0]).collect(Collectors.toList());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            sheet.setZoom(80);

            int rowNum = 0;
            XSSFRow row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String header : headerList) {
                XSSFCell cell = row.createCell(colNum++);
                cell.setCellValue(header);
            }


            for (Map<String, String> map : mapList) {
                colNum = 0;
                row = sheet.createRow(rowNum++);
                for (String key : map.keySet()) {
                    XSSFCell cell = row.createCell(colNum++);
                    cell.setCellValue(map.get(key).split("\\|")[1]);
                }
            }

            FileOutputStream outputStream = new FileOutputStream(xlsxFile, false);

            workbook.write(outputStream);


        } catch (IOException e) {
            System.out.println("ERROR With XLSX Writing/Creating Process");
        }

        return xlsxFile;
    }
}
















