package com.nayak.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class XpathColumnNameModel {
    String xpath;
    String excelColumn;
    String excelColumNumberedArray;
}
