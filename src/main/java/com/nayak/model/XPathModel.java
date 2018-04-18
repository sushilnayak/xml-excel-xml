package com.nayak.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class XPathModel {
    String xpath;
    String value;
}
