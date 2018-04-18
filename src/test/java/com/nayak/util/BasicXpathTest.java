package com.nayak.util;

import com.nayak.model.XPathModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.*;

@RunWith(JUnit4.class)
public class BasicXpathTest {
    String xmlString;
    File xmlFile;

    File soapUIXMLRequestFile;
    File soapUIXMLResponseFile;

    @Before
    public void setUp() {
        soapUIXMLResponseFile = new File("src/test/resources/gs-spring-soapuisample-response.xml");
        soapUIXMLRequestFile = new File("src/test/resources/gs-spring-soapuisample-request.xml");
        xmlFile = new File("src/test/resources/dummy.xml");
    }

    @Test
    public void basic() throws XMLStreamException, FileNotFoundException {

        List<XPathModel> xmlList = new XpathGen().xmlToXpath(soapUIXMLRequestFile);


        xmlList.stream().forEach(System.out::println);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream(soapUIXMLRequestFile));
        Map<String, String> namespaces = new HashMap<>();
        while (reader.hasNext()) {
            int evt = reader.next();
            if (evt == XMLStreamConstants.START_ELEMENT) {
                QName qName = reader.getName();
                if (qName != null) {
                    if (qName.getPrefix() != null && qName.getPrefix().compareTo("") != 0)
                        namespaces.put(qName.getPrefix(), qName.getNamespaceURI());
                }
            }
        }
        System.out.println(namespaces);

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(new FileInputStream(soapUIXMLRequestFile));

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

            String expression = "//gs:getCountryRequest[1]/gs:name[1]";

            String email = xPath.compile(expression).evaluate(document);

            Node node = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);
            node.setTextContent("India");
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(sw));
            System.out.println(sw.toString());

            System.out.println(node.getTextContent());
            System.out.println("Email =>>>> " + email);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
