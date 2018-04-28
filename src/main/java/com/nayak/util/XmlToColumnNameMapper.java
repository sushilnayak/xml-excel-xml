package com.nayak.util;

import com.nayak.model.XPathModel;
import com.nayak.model.XpathColumnNameModel;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class XmlToColumnNameMapper {

    private XmlToColumnNameMapper() {
    }

    public static List<XpathColumnNameModel> xmlToXpath(String xmlString) {
        return app(new XpathGen().xmlToXpath(xmlString),"");
    }

    public static List<XpathColumnNameModel> xmlToXpath(File xmlFile) {
        return app(new XpathGen().xmlToXpath(xmlFile),"");
    }

    public static List<XpathColumnNameModel> xmlToXpath(String xmlString, String columnPrefix) {
        return app(new XpathGen().xmlToXpath(xmlString),columnPrefix);
    }

    public static List<XpathColumnNameModel> xmlToXpath(File xmlFile, String columnPrefix) {
        return app(new XpathGen().xmlToXpath(xmlFile),columnPrefix);
    }

    private static List<XpathColumnNameModel> app(List<XPathModel> xPathModelList, String columnPrefix) {

        List<XPATHModelExtended> xpathModelExtendedList = xPathModelList.stream().map(x -> new XPATHModelExtended(x.getXpath())).collect(Collectors.toList());
        List<XPATHModelExtended> c = new ArrayList<>();

        boolean solved = xpathModelExtendedList.stream()
                .map(XPATHModelExtended::isSolved)
                .reduce((Boolean b1, Boolean b2) -> b1 && b2)
                .get();

        while (!solved) {

            List<XPATHModelExtended> x = xpathModelExtendedList.stream().map(a -> {
                if (!a.isSolved()) {
                    List<String> y = new ArrayList<>(a.getXpathArray());
                    y.remove(0);
                    a.setPrevXpathArray(a.getXpathArray());
                    a.setXpathArray(y);
                }
                return a;
            }).collect(Collectors.toList());

            c = x.stream().map(a -> {
                if (!a.isSolved()) {
                    String newString = String.join("_", a.getXpathArray());
                    String prevString = String.join("_", a.getPrevXpathArray());

                    Long count = x.stream().filter(b -> String.join("_", b.getXpathArray()).equals(newString)).count();

                    if (count > 1) {
                        a.setSolved(true);
                        a.setExcelColumn(prevString);
                    }
                    if (count == 1 && a.getXpathArray().size() == 1) {
                        a.setSolved(true);
                        a.setExcelColumn(newString);
                    }
                }
                return a;
            }).collect(Collectors.toList());

            solved  = xpathModelExtendedList.stream()
                    .map(XPATHModelExtended::isSolved)
                    .reduce((Boolean b1, Boolean b2) -> b1 && b2)
                    .get();
        }

        return  c.stream().map(x-> XpathColumnNameModel.builder()
                    .xpath(x.getXpath())
                    .excelColumNumberedArray(columnPrefix + x.getExcelColumn().replace("[","_").replace("]",""))
                    .excelColumn(columnPrefix + x.getExcelColumn().replaceAll("\\[\\d+\\]$","").replace("[","_").replace("]",""))
                    .build() ).collect(Collectors.toList());

    }


    @Data
    private static class XPATHModelExtended {
        List<String> xpathArray;
        List<String> prevXpathArray;
        String xpath;
        Integer count;
        boolean solved;
        String excelColumn;

        public XPATHModelExtended(String xpath) {
            this.xpath = xpath;
            xpathArray = Arrays.asList(xpath.substring(2).split("/"));
            prevXpathArray = new ArrayList<>();
            this.solved = false;
            this.count = this.xpathArray.size();
            this.excelColumn = "";
        }
    }
}
