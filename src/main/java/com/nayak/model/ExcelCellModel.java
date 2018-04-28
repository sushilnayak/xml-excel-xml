package com.nayak.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExcelCellModel {
    private String cellValue;
    private String cellColor;
    private String cellName;
}