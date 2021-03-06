package com.nayak.util;

import com.nayak.model.CDATAXmlModel;
import com.nayak.model.XPathModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class CommonUtilsTest {

    String xmlInsideCdata;
    String xmlString;
    String htmlTagsBasedXML;

    @Before
    public void setUp() {

        htmlTagsBasedXML = "<root><input><name>sushil</name><age-group>young</age-group><lname>nayak</lname></input></root>";

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
    public void cdataXmlXpathGenerationTest() {

        CDATAXmlModel cdataXmlModel = CommonUtils.xmlWithNestedCdataXmlXpathInfo(xmlInsideCdata);
        CDATAXmlModel noCdataXmlModel = CommonUtils.xmlWithNestedCdataXmlXpathInfo(xmlString);

        assertThat(noCdataXmlModel.isHasCdata(), equalTo(false));
        assertThat(cdataXmlModel.isHasCdata(), equalTo(true));
    }

    @Test
    public void cdataPopulateRecreationTest() {

        CDATAXmlModel cdataXmlModel = CommonUtils.xmlWithNestedCdataXmlXpathInfo(xmlInsideCdata);

        // Check if CDATA Is there or not!
        assertThat(cdataXmlModel.isHasCdata(), equalTo(true));

        String xml = cdataXmlModel.getCleanedUpXml().replace("></", ">?</");

        Map<String, String> xMap = new XpathGen().xmlToXpath(xml)
                .stream()
                .collect(Collectors.toMap(x -> x.getXpath(), x -> "999999"));

        String cdataXml = CommonUtils.backWithUpdatedCdataXml(cdataXmlModel, xMap);

        assertThat(new XpathGen().xmlToXpath(cdataXml).size(), equalTo(3));

    }

    @Test
    public void xmlHavingHTMLTags() {
        List<XPathModel> xPathModel = new XpathGen().xmlToXpath(CommonUtils.formatXML(htmlTagsBasedXML));

        assertThat(3, equalTo(xPathModel.size()));

        assertThat(xPathModel, hasItem(XPathModel.builder().xpath("//root[1]/input[1]/name[1]").value("sushil").build()));
        assertThat(xPathModel, hasItem(XPathModel.builder().xpath("//root[1]/input[1]/age-group[1]").value("young").build()));
        assertThat(xPathModel, hasItem(XPathModel.builder().xpath("//root[1]/input[1]/lname[1]").value("nayak").build()));
    }


}
