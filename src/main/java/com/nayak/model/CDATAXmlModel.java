package com.nayak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CDATAXmlModel {

    String rawXml;
    String cleanedUpXml;
    List<String> cdataValueHoldingElementXpath;
    @Builder.Default
    private boolean hasCdata = false;

}