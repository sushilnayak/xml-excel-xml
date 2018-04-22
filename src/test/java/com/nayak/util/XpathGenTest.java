package com.nayak.util;


import com.nayak.model.XPathModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class XpathGenTest {
    String xmlString;
    File xmlFile;

    File soapUIXMLRequestFile;
    File soapUIXMLResponseFile;
    @Before
    public void setUp() {
        soapUIXMLResponseFile=new File("src/test/resources/gs-spring-soapuisample-response.xml");
        soapUIXMLRequestFile=new File("src/test/resources/gs-spring-soapuisample-request.xml");
        xmlFile = new File("src/test/resources/test.xml");
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
    public void xPathVerifiedForSoapUIXMLResponseFile(){
        List<XPathModel> xmlList=new XpathGen().xmlToXpath(soapUIXMLRequestFile);
        xmlList.stream().forEach(System.out::println);

        assertThat(xmlList.size(),equalTo(1));
    }
    @Test
    public void xPathVerifiedForSoapUIXMLRequestFile(){
        List<XPathModel> xmlList=new XpathGen().xmlToXpath(soapUIXMLResponseFile);
        xmlList.stream().forEach(System.out::println);
        assertThat(xmlList.size(),equalTo(4));
    }

    @Test
    public void test(){
        List<String> x1= Arrays.asList("a","b","c");
        List<String> x4= Arrays.asList("a","b","c");
        List<String> x2= Arrays.asList("c","d","c");
        List<String> x3= Arrays.asList("a","e","d");

        List<List<String>> x=Arrays.asList(x1,x2,x3,x4);

        Map<String,Long> xxxx= x.stream().map(y -> y.toString()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println(xxxx);
    }

}
