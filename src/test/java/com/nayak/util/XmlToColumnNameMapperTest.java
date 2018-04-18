package com.nayak.util;

import com.nayak.model.XpathColumnNameModel;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class XmlToColumnNameMapperTest {
    File xmlFile;
    File soapUIXMLRequestFile;
    File soapUIXMLResponseFile;
    @Before
    public void setUp() {
        soapUIXMLResponseFile=new File("src/test/resources/gs-spring-soapuisample-response.xml");
        soapUIXMLRequestFile=new File("src/test/resources/gs-spring-soapuisample-request.xml");
        xmlFile=new File("src/test/resources/weird.xml");
    }

    @Test
    public void basic(){
        List<XpathColumnNameModel> list= XmlToColumnNameMapper.xmlToXpath(xmlFile);
        assertThat(list.size(), equalTo(14));
    }

    @Test
    public void basicSoapUIXMlRequest(){
        List<XpathColumnNameModel> list= XmlToColumnNameMapper.xmlToXpath(soapUIXMLRequestFile);
        assertThat(list.size(), equalTo(1));
        assertEquals(list.get(0).getExcelColumn(),"gs:name");
        assertEquals(list.get(0).getExcelColumNumberedArray(),"gs:name_1");
        assertEquals(list.get(0).getXpath(),"//soapenv:Envelope[1]/soapenv:Body[1]/gs:getCountryRequest[1]/gs:name[1]");
    }

    @Test
    public void basicSoapUIXMlResponse(){
        List<XpathColumnNameModel> list= XmlToColumnNameMapper.xmlToXpath(soapUIXMLResponseFile);
//        list.stream().forEach(System.out::println);
        assertThat(list.size(), equalTo(4));
        assertEquals(list.get(0).getExcelColumn(),"ns2:name");
        assertEquals(list.get(0).getExcelColumNumberedArray(),"ns2:name_1");
        assertEquals(list.get(0).getXpath(),"//SOAP-ENV:Envelope[1]/SOAP-ENV:Body[1]/ns2:getCountryResponse[1]/ns2:country[1]/ns2:name[1]");
    }
}
