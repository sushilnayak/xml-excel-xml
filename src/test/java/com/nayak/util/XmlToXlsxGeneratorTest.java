package com.nayak.util;

import com.nayak.model.XPathModel;
import com.nayak.model.XpathColumnNameModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class XmlToXlsxGeneratorTest {
    String xmlString;
    String xmlStringOutofSync;
    String xmlString2;

    @Before
    public void setUp() {

        xmlString="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<employee>\n" +
                "    <name>?</name>\n" +
                "    <salary>?</salary>\n" +
                "    <designation>?</designation>\n" +
                "    <address>\n" +
                "        <city>?</city>\n" +
                "        <line1>?</line1>\n" +
                "        <state>?</state>\n" +
                "        <zipCode>?</zipCode>\n" +
                "    </address>\n" +
                "    <address>\n" +
                "        <city>?</city>\n" +
                "        <line1>?</line1>\n" +
                "        <state>?</state>\n" +
                "        <zipCode>?</zipCode>\n" +
                "    </address>\n" +
                "</employee>\n";

        xmlString2="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<employee>\n" +
                "    <name>Sushil</name>\n" +
                "    <salary>?</salary>\n" +
                "    <designation>?</designation>\n" +
                "    <address>\n" +
                "        <city>?</city>\n" +
                "        <line1>?</line1>\n" +
                "        <state>?</state>\n" +
                "        <zipCode>?</zipCode>\n" +
                "    </address>\n" +
                "    <address>\n" +
                "        <city>?</city>\n" +
                "        <line1>?</line1>\n" +
                "        <state>?</state>\n" +
                "        <zipCode>22222</zipCode>\n" +
                "    </address>\n" +
                "</employee>\n";

        xmlStringOutofSync="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<employee>\n" +
                "    <name>?</name>\n" +
                "    <salary>?</salary>\n" +
                "    <designation>?</designation>\n" +
                "    <address>\n" +
                "        <city>?</city>\n" +
                "        <line1>?</line1>\n" +
                "        <state>?</state>\n" +
                "        <zipCode>?</zipCode>\n" +
                "    </address>\n" +
                "</employee>\n";
    }

    @Test
    public void allSameXML(){
        List<String> x= Arrays.asList(xmlString,xmlString,xmlString);
        boolean wassup=XmlToXlsxGenerator.validateXMLSameSchema(x);
        assertThat(wassup, equalTo(true));
    }

    @Test
    public void testingIncorrectSchemainBetween(){
        List<String> x= Arrays.asList(xmlString,xmlString,xmlStringOutofSync);
        boolean wassup=XmlToXlsxGenerator.validateXMLSameSchema(x);
        assertThat(wassup, equalTo(false));
    }


}
