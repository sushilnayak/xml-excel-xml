package com.nayak.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.*;

public class CommonUtils {

    private CommonUtils() {
    }

    public static List<Map<String, String>> readXLSXToListMap(File file) {

        List<Map<String, String>> mapList = new ArrayList<>();
        try (
                Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<Integer, String> headerMap = new HashMap<>();
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);


                Map<String, String> dataMap = new LinkedHashMap<>();

                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    String rowData = "";

                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        rowData = cell.getStringCellValue();
                    } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {

                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            rowData = cell.getDateCellValue().toString();
                        } else {
                            rowData = NumberToTextConverter.toText(cell.getNumericCellValue());
                        }
                    } else if (cell.getCellTypeEnum() == CellType.BLANK || cell.getCellTypeEnum() == CellType._NONE) {
                        rowData = cell.getStringCellValue();
                    } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
                        rowData = String.valueOf(cell.getBooleanCellValue());
                    }


                    if (i == 0) {
                        headerMap.put(j, rowData);
                    } else {
                        dataMap.put(headerMap.get(j), rowData);
                    }
                }

                if (i != 0) mapList.add(dataMap);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapList;
    }


    public static String XmlUpdateUsingXpath(String xmlString, Map<String,String> expressionData){
        StringWriter stringWriter = new StringWriter();
        try{
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
//        Document document = builder.parse(new FileInputStream(xmlFile));
            Document document=builder.parse(new InputSource(new StringReader(xmlString)));

            Map<String, String> namespaces = getNamepsacesfromXML(xmlString);

            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {

                public String getNamespaceURI(String prefix) {
                    if (prefix == null)
                        throw new NullPointerException("Null prefix");
                    else if (namespaces.containsKey(prefix)) return namespaces.get(prefix);
                    else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
                    else if (prefix.length() > 0) return "http://unknown/but/needtolook/" + prefix;
                    return XMLConstants.NULL_NS_URI;
                }

                public String getPrefix(String uri) {
                    throw new UnsupportedOperationException();
                }

                public Iterator getPrefixes(String uri) {
                    throw new UnsupportedOperationException();
                }
            });

            Node node;

            for (Map.Entry<String,String> entry : expressionData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                node = (Node) xPath.compile(key).evaluate(document, XPathConstants.NODE);
                if(node!=null){
                    node.setTextContent(value);
                }
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        }
        catch (ParserConfigurationException | XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


        return stringWriter.toString();
    }

    public static Map<String,String> getNamepsacesfromXML(File xmlFile){
        Map<String, String> namespaces = new HashMap<>();
        try{
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream(xmlFile));
            while (reader.hasNext()) {
                int evt = reader.next();
                if (evt == XMLStreamConstants.START_ELEMENT) {
                    QName qName = reader.getName();
                    if (qName != null && qName.getPrefix() != null && qName.getPrefix().compareTo("") != 0){
                        namespaces.put(qName.getPrefix(), qName.getNamespaceURI());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return namespaces;
    }

    public static Map<String,String> getNamepsacesfromXML(String xmlString){
        Map<String, String> namespaces = new HashMap<>();
        try{
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xmlString));
            while (reader.hasNext()) {
                int evt = reader.next();
                if (evt == XMLStreamConstants.START_ELEMENT) {
                    QName qName = reader.getName();
                    if (qName != null && qName.getPrefix() != null && qName.getPrefix().compareTo("") != 0){
                        namespaces.put(qName.getPrefix(), qName.getNamespaceURI());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return namespaces;
    }

}
