package com.nayak.util;

import com.nayak.model.XPathModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates Xpath based on the input XML file/String provided by the user
 *
 * @see XmlToColumnNameMapper
 */
public class XpathGen {

    private final Logger log = LoggerFactory.getLogger(XpathGen.class);

    private List<XPathModel> xPathModelList;

    public XpathGen() {
        this.xPathModelList = new ArrayList<>();
    }

    public List<XPathModel> xmlToXpath(String xmlString) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            xmlReader.setContentHandler(new FragmentContentHandler(xmlReader));
            xmlReader.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("There was some error in producing Xpaths");
            e.printStackTrace();
        }

        return xPathModelList;
    }

    public List<XPathModel> xmlToXpath(File xmlFile) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            xmlReader.setContentHandler(new FragmentContentHandler(xmlReader));
            xmlReader.parse(new InputSource(new InputStreamReader(new FileInputStream(xmlFile), "UTF-8")));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("There was some error in producing Xpaths");
            e.printStackTrace();
        }

        return xPathModelList;
    }


    private class FragmentContentHandler extends DefaultHandler {
        String xPath = "/";
        XMLReader xmlReader;
        FragmentContentHandler parent;
        StringBuilder characters = new StringBuilder();
        Map<String, Integer> elementNameCount = new HashMap<String, Integer>();

        public FragmentContentHandler(XMLReader xmlReader) {
            this.xmlReader = xmlReader;
        }

        private FragmentContentHandler(String xPath, XMLReader xmlReader, FragmentContentHandler parent) {
            this(xmlReader);
            this.xPath = xPath;
            this.parent = parent;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            Integer count = elementNameCount.get(qName);
            if (null == count) {
                count = 1;
            } else {
                count++;
            }
            elementNameCount.put(qName, count);
            String childXPath = xPath + "/" + qName + "[" + count + "]";

            int attsLength = atts.getLength();
            for (int x = 0; x < attsLength; x++) {
                if(!atts.getQName(x).startsWith("xmlns:")){
                    xPathModelList.add(XPathModel.builder().xpath(childXPath + "[@" + atts.getQName(x)).value(atts.getValue(x)).build());
                }
            }

            FragmentContentHandler child = new FragmentContentHandler(childXPath, xmlReader, this);
            xmlReader.setContentHandler(child);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String value = characters.toString().trim();
            if (value.length() > 0) {
                xPathModelList.add(XPathModel.builder().xpath(xPath).value(characters.toString()).build());
            }
            xmlReader.setContentHandler(parent);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            characters.append(ch, start, length);
        }
    }

}
