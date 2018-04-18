package com.nayak.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

@RunWith(JUnit4.class)
public class XLSXFileGeneratorTest {
    String xmlString;
    File xmlFile;
    File soapUIXMLRequestFile;
    File soapUIXMLResponseFile;
    @Before
    public void setUp() {
        soapUIXMLResponseFile=new File("src/test/resources/gs-spring-soapuisample-response.xml");
        soapUIXMLRequestFile=new File("src/test/resources/gs-spring-soapuisample-request.xml");
        xmlFile = new File("src/test/resources/test.xml");
    }
    @Test
    public void basicTest(){
        File file=new File(System.getProperty("user.dir") + "/out/test.xlsx");
        XLSXFileGenerator.generateXLSXForTestingSOAP(xmlFile,xmlFile,file,false,"EXPECTED_");
    }

    @Test
    public void soapUIGSWSTest(){
        File file=new File(System.getProperty("user.dir") + "/out/soapui.xlsx");
        XLSXFileGenerator.generateXLSXForTestingSOAP(soapUIXMLRequestFile,soapUIXMLResponseFile,file,false,"EXPECTED_");
    }
}
