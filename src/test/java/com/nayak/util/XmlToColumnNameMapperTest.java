package com.nayak.util;

import com.nayak.model.XpathColumnNameModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class XmlToColumnNameMapperTest {
    File xmlFile;
    File soapUIXMLRequestFile;
    File soapUIXMLResponseFile;
    String soapString;
    String xmlInsideCdata;
    String xmlInsideCdata1;
    File randomeXMLFile;
    File randomeComplexXMLFile;
    String xmlInsideCdata2;

    @Before
    public void setUp() {
        randomeXMLFile = new File("src/test/resources/random.xml");
        randomeComplexXMLFile = new File("src/test/resources/random-complex.xml");
        soapUIXMLResponseFile = new File("src/test/resources/gs-spring-soapuisample-response.xml");
        soapUIXMLRequestFile = new File("src/test/resources/gs-spring-soapuisample-request.xml");
        xmlFile = new File("src/test/resources/weird.xml");

        soapString = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <SOAP-ENV:Header/>\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns2:getCountryResponse xmlns:ns2=\"http://spring.io/guides/gs-producing-web-service\">\n" +
                "            <ns2:country>\n" +
                "                <ns2:name>Spain</ns2:name>\n" +
                "                <ns2:population>46704314</ns2:population>\n" +
                "                <ns2:capital>Madrid</ns2:capital>\n" +
                "                <ns2:currency>EUR</ns2:currency>\n" +
                "            </ns2:country>\n" +
                "        </ns2:getCountryResponse>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
        //TODO: There is some xpath generation issue with this xml data.. need to add solution to cater to this..
        xmlInsideCdata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://earth.google.com/kml/2.0\">\n" +
                "    <Document>\n" +
                "        <name>area Area Date: 2014-07-31</name>\n" +
                "        <Placemark><name>P07L327</name><Point><coordinates>-96.26879,85.19125</coordinates></Point><description><![CDATA[<ol><li> Data Loaded:  NO</li><li>Quality: 5</li><li>Status: UP</li><li>Index: 72</li></eol>]]></description><Style> id = \"colorIcon\"</Style></Placemark>\n" +
                "        <coordinates>-96.26879,85.19125,0 -96.26879,85.19125,0 -96.26879,85.19125,0 -96.26879,85.19125,0 -96.26879,45.14698,0 </coordinates>\n" +
                "    </Document>\n" +
                "</kml>";
        xmlInsideCdata1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <SOAP-ENV:Header/>\n" +
                "    <SOAP-ENV:Body>\n" +
                "        <ns2:getCountryResponse xmlns:ns2=\"http://spring.io/guides/gs-producing-web-service\">\n" +
                "            <ns2:country>\n" +
                "                <ns2:name>Spain</ns2:name>\n" +
                "<item><![CDATA[\n" +
                "                <ns2:population>46704314</ns2:population>\n" +
                "                <ns2:capital>Madrid</ns2:capital>\n" +
                "                <ns2:currency>EUR</ns2:currency>\n" +
                "]]></item>\n" +
                "            </ns2:country>\n" +
                "        </ns2:getCountryResponse>\n" +
                "    </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";

        xmlInsideCdata2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n" +
                "   xmlns:sam=\"http://www.example.org/sample/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <sam:searchResponse>\n" +
                "         <sam:searchResponse>\n" +
                "            <item><id>1234</id><id><![CDATA[<item><width>123</width><height>345</height>\n" +
                "<length>098</length><isle>A34</isle></item>]]></id><price>123</price>\n" +
                "            </item>\n" +
                "         </sam:searchResponse>\n" +
                "      </sam:searchResponse>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }

    @Test
    public void basic() {
        List<XpathColumnNameModel> list = XmlToColumnNameMapper.xmlToXpath(xmlFile);
        assertThat(list.size(), equalTo(14));
    }

    @Test
    public void basicSoapUIXMlRequest() {
        List<XpathColumnNameModel> list = XmlToColumnNameMapper.xmlToXpath(soapUIXMLRequestFile);
        assertThat(list.size(), equalTo(1));
        assertEquals("gs:name", list.get(0).getExcelColumn());
        assertEquals("gs:name_1", list.get(0).getExcelColumNumberedArray());
        assertEquals("//soapenv:Envelope[1]/soapenv:Body[1]/gs:getCountryRequest[1]/gs:name[1]", list.get(0).getXpath());
    }

    @Test
    public void basicSoapUIXMlResponse() {
        List<XpathColumnNameModel> list = XmlToColumnNameMapper.xmlToXpath(soapUIXMLResponseFile);

        assertThat(list.size(), equalTo(4));
        assertEquals(list.get(0).getExcelColumn(), "ns2:name");
        assertEquals(list.get(0).getExcelColumNumberedArray(), "ns2:name_1");
        assertEquals(list.get(0).getXpath(), "//SOAP-ENV:Envelope[1]/SOAP-ENV:Body[1]/ns2:getCountryResponse[1]/ns2:country[1]/ns2:name[1]");
    }


    @Test
    public void normalizedbasicSoapUIXMlResponse() {

        List<XpathColumnNameModel> list = XmlToColumnNameMapper.xmlToXpath(CommonUtils.formatXML(soapString).replace("></", ">?</"));

        assertThat(list.size(), equalTo(4));
        assertEquals(list.get(0).getExcelColumn(), "ns2:name");
        assertEquals(list.get(0).getExcelColumNumberedArray(), "ns2:name_1");
        assertEquals(list.get(0).getXpath(), "//SOAP-ENV:Envelope[1]/SOAP-ENV:Body[1]/ns2:getCountryResponse[1]/ns2:country[1]/ns2:name[1]");
    }

    @Test
    public void cdataSoapUIXMlResponse() {

        List<XpathColumnNameModel> list = XmlToColumnNameMapper.xmlToXpath(CommonUtils.formatXML(xmlInsideCdata2).replace("></", ">?</"));

        list.stream().forEach(System.out::println);
    }

}
