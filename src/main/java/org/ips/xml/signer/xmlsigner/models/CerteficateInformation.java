package org.ips.xml.signer.xmlsigner.models;

import lombok.Data;

import java.security.cert.X509Certificate;

@Data
public class CerteficateInformation {
    private String certificateIssuer;

    private String certificateSerialNumber;

    private X509Certificate x509Certificate;

    private String certificate;

    private String certeficateDownloadUrl;

    private String validToken;
}
