package com.nayak.util;

import com.nayak.model.CDATAXmlModel;
import com.nayak.model.ExcelCellModel;
import com.nayak.model.XPathModel;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import javax.xml.transform.*;
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


    public static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    public static String backWithUpdatedCdataXml(CDATAXmlModel cdataXmlModel, Map<String, String> xMap) {

        String xml = cdataXmlModel.getCleanedUpXml().replace("></", ">?</");

        String updatedXml = CommonUtils.XmlUpdateUsingXpath(xml, xMap);

        String cdataXml = null;

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(updatedXml)));
            Map<String, String> namespaces = CommonUtils.getNamepsacesfromXML(updatedXml);

            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {

                public String getNamespaceURI(String prefix) {
                    if (prefix == null)
                        throw new NullPointerException("Null prefix");
                    else if (namespaces.containsKey(prefix)) return namespaces.get(prefix);
                    else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
                    return XMLConstants.NULL_NS_URI;
                }

                public String getPrefix(String uri) {
                    throw new UnsupportedOperationException();
                }

                public Iterator getPrefixes(String uri) {
                    throw new UnsupportedOperationException();
                }
            });

            for (String expression : cdataXmlModel.getCdataValueHoldingElementXpath()) {

                Element node = (Element) xPath.compile(expression).evaluate(document, XPathConstants.NODE);

                // failed Implementation #1
//                Node nodexx=node.getParentNode();
//                node.getParentNode().removeChild(node);
//                nodexx.appendChild(document.createCDATASection(nodeToString(node)));

                // failed Implementation #2
//                node.getParentNode().replaceChild( document.createCDATASection(nodeToString(node)) , node);

                // Implementation #3
                StringBuilder nodeString = new StringBuilder();
                while (node.hasChildNodes()) {
                    nodeString.append(nodeToString(node.getFirstChild()).trim());
                    node.removeChild(node.getFirstChild());
                }
                node.appendChild(document.createCDATASection(nodeString.toString()));

            }
            cdataXml = nodeToString(document);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cdataXml;
    }

    public static String formatXML(String xmlString) {
        StringWriter stringWriter = new StringWriter();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
            //Normalize the XML Structure
            document.getDocumentElement().normalize();
            // Trimming Node Text values
            trimWhitespace(document.getDocumentElement());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "html");

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    private static void trimWhitespace(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
            }
            trimWhitespace(child);
        }
    }

    public static List<Map<String, ExcelCellModel>> readXLSXToListMap(File file) {

        List<Map<String, ExcelCellModel>> mapList = new ArrayList<>();
        try (
                Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<Integer, String> headerMap = new HashMap<>();
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                Map<String, ExcelCellModel> dataMap = new LinkedHashMap<>();

                for (int j = 0; j < row.getLastCellNum(); j++) {

                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    if (i == 0) {
                        headerMap.put(j, fetchExcelCellValue(cell));
                    } else {
                        dataMap.put(headerMap.get(j), ExcelCellModel.builder()
                                .cellValue(fetchExcelCellValue(cell))
                                .cellName(headerMap.get(j))
                                .cellColor(fetchExcelCellColor(cell))
                                .build());
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

    public static String fetchExcelCellColor(Cell cell) {
        CellStyle cellStyle = cell.getCellStyle();

        Color color = cellStyle.getFillForegroundColorColor();
        String colorString = "";
        if (color != null) {
            if (color instanceof XSSFColor) {
                colorString = ((XSSFColor) color).getARGBHex();
            } else if (color instanceof HSSFColor) {
                if (!(color instanceof HSSFColor.AUTOMATIC))
                    colorString = ((HSSFColor) color).getHexString();
            }
        }
        return colorString;
    }

    public static String fetchExcelCellValue(Cell cell) {
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

        return rowData;
    }

    public static String XmlUpdateUsingXpath(String xmlString, Map<String, String> expressionData) {
        StringWriter stringWriter = new StringWriter();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
//        Document document = builder.parse(new FileInputStream(xmlFile));
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

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

            for (Map.Entry<String, String> entry : expressionData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                node = (Node) xPath.compile(key).evaluate(document, XPathConstants.NODE);
                if (node != null) {
                    node.setTextContent(value);
                }
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        } catch (ParserConfigurationException | XPathExpressionException | TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    public static Map<String, String> getNamepsacesfromXML(File xmlFile) {
        Map<String, String> namespaces = new HashMap<>();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream(xmlFile));
            while (reader.hasNext()) {
                int evt = reader.next();
                if (evt == XMLStreamConstants.START_ELEMENT) {
                    QName qName = reader.getName();
                    if (qName != null && qName.getPrefix() != null && qName.getPrefix().compareTo("") != 0) {
                        namespaces.put(qName.getPrefix(), qName.getNamespaceURI());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return namespaces;
    }

    public static Map<String, String> getNamepsacesfromXML(String xmlString) {
        Map<String, String> namespaces = new HashMap<>();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xmlString));
            while (reader.hasNext()) {
                int evt = reader.next();
                if (evt == XMLStreamConstants.START_ELEMENT) {
                    QName qName = reader.getName();
                    if (qName != null && qName.getPrefix() != null && qName.getPrefix().compareTo("") != 0) {
                        namespaces.put(qName.getPrefix(), qName.getNamespaceURI());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return namespaces;
    }

    public static String soapEnvelopseNamespace(File xmlFile) {
        String soapenvelop = "";
        Map<String, String> namespacesMap = CommonUtils.getNamepsacesfromXML(xmlFile);
        for (String header : namespacesMap.keySet()) {

            if (namespacesMap.getOrDefault(header, "").toLowerCase().contains("http://schemas.xmlsoap.org/soap/envelope")) {
                soapenvelop = header;
            }

        }
        return soapenvelop;
    }

    public static String soapEnvelopseNamespace(String xmlString) {
        String soapenvelop = "";
        Map<String, String> namespacesMap = CommonUtils.getNamepsacesfromXML(xmlString);
        for (String header : namespacesMap.keySet()) {

            if (namespacesMap.getOrDefault(header, "").toLowerCase().contains("http://schemas.xmlsoap.org/soap/envelope")) {
                soapenvelop = header;
            }

        }
        return soapenvelop;
    }

    public static CDATAXmlModel cdataXpathInfo(String xmlString) {

        boolean hasCdata = false;
        List<String> cdataValueHoldingElementXpath = new ArrayList<>();
        StringWriter stringWriter = new StringWriter();
        try {

            List<XPathModel> xmlList = new XpathGen().xmlToXpath(xmlString.replace("></", ">?</"));

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            Map<String, String> namespaces = CommonUtils.getNamepsacesfromXML(xmlString);

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

            for (XPathModel entry : xmlList) {
                String key = entry.getXpath();
                Element node = (Element) xPath.compile(key).evaluate(document, XPathConstants.NODE);
//                Node.CDATA_SECTION_NODE == 4
                if (node != null) {
                    if (node.getFirstChild().getNodeType() == Node.CDATA_SECTION_NODE) {
                        hasCdata = true;
                        cdataValueHoldingElementXpath.add(key);
                        node.setTextContent(node.getTextContent());
                    }
                }
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return CDATAXmlModel.builder()
                .hasCdata(hasCdata)
                .rawXml(xmlString)
                .cleanedUpXml(CommonUtils.formatXML(stringWriter.toString().replace("&lt;", "<").replace("&gt;", ">")))
                .cdataValueHoldingElementXpath(cdataValueHoldingElementXpath)
                .build();
    }


}
