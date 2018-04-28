package com.nayak.util;

import com.nayak.model.XPathModel;
import org.junit.Before;
import org.junit.Ignore;
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

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
    public void basic(){
        assertThat(1,equalTo(1));
   }


}
