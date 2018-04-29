package com.nayak.util;


import com.nayak.model.CDATAXmlModel;
import com.nayak.model.XPathModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class XpathGenTest {
    String xmlString;
    File xmlFile;

    File soapUIXMLRequestFile;
    File soapUIXMLResponseFile;

    String xmlInsideCdata;

    @Before
    public void setUp() {
        soapUIXMLResponseFile = new File("src/test/resources/gs-spring-soapuisample-response.xml");
        soapUIXMLRequestFile = new File("src/test/resources/gs-spring-soapuisample-request.xml");
        xmlFile = new File("src/test/resources/test.xml");

        xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
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

        xmlInsideCdata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n" +
                "   xmlns:sam=\"http://www.example.org/sample/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <sam:searchResponse>\n" +
                "         <sam:searchResponse>\n" +
                "            <item><id>1234</id><description><![CDATA[<item><width>123</width><height>345</height>\n" +
                "<length>098</length><isle>A34</isle></item>]]></description><price>123</price>\n" +
                "            </item>\n" +
                "         </sam:searchResponse>\n" +
                "      </sam:searchResponse>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

    }

    @Test
    public void xPathContentVerifierFromXMLFile() {
        List<XPathModel> xmlList = new XpathGen().xmlToXpath(xmlFile);
        XPathModel xPathModel = XPathModel.builder().xpath("//employee[1]/address[2]/zipCode[1]").value("?").build();
        assertThat(xmlList, hasItem(xPathModel));
    }


    @Test
    public void xPathContentVerifierFromXMLString() {
        List<XPathModel> xmlList = new XpathGen().xmlToXpath(xmlString);
        XPathModel xPathModel = XPathModel.builder().xpath("//employee[1]/address[2]/zipCode[1]").value("?").build();

        assertThat(xmlList, hasItem(xPathModel));
    }

    @Test
    public void xPathVerifierFromXMLFile() {
        List<XPathModel> xmlList = new XpathGen().xmlToXpath(xmlFile);
        assertThat(xmlList.size(), equalTo(11));
    }

    @Test
    public void xPathVerifierFromXMLString() {
        List<XPathModel> xmlList = new XpathGen().xmlToXpath(xmlString);
        assertThat(xmlList.size(), equalTo(11));
    }

    @Test
    public void xPathVerifiedForSoapUIXMLResponseFile() {
        List<XPathModel> xmlList = new XpathGen().xmlToXpath(soapUIXMLRequestFile);
//        xmlList.stream().forEach(System.out::println);
        assertThat(xmlList.size(), equalTo(1));
    }

    @Test
    public void xPathVerifiedForSoapUIXMLRequestFile() {
        List<XPathModel> xmlList = new XpathGen().xmlToXpath(soapUIXMLResponseFile);
//        xmlList.stream().forEach(System.out::println);
        assertThat(xmlList.size(), equalTo(4));
    }


    @Test
    public void cdataXmlXpathGenerationTest() {

        CDATAXmlModel  cdataXmlModel = CommonUtils.xmlWithNestedCdataXmlXpathInfo(xmlInsideCdata);

        List<XPathModel> xx = new XpathGen().xmlToXpath(cdataXmlModel.getCleanedUpXml().replace("></", ">?</"));
        assertThat(xx.size(), equalTo(6));
        assertThat(xx, hasItem(XPathModel.builder()
                .xpath("//soapenv:Envelope[1]/soapenv:Body[1]/sam:searchResponse[1]/sam:searchResponse[1]/item[1]/description[1]/item[1]/length[1]")
                .value("098")
                .build()));
    }

}
