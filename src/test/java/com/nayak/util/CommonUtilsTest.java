package com.nayak.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class CommonUtilsTest {

    @Test
    public void excelRead(){
        List<Map<String, String>> mapList = CommonUtils.readXLSXToListMap(new File("c:\\dev\\dummy.xlsx"));
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-");
        for (Map<String,String> m: mapList ) {
            System.out.println(m);
        }
    }
}
