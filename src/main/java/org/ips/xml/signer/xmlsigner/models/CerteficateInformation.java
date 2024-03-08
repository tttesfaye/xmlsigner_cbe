package org.ips.xml.signer.xmlsigner.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CerteficateInformation {
    private String certificateIssuer;
    private String certificateSerialNumber;
    private String certificate;
}
