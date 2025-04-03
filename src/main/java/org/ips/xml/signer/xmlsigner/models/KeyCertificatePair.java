package org.ips.xml.signer.xmlsigner.models;

import lombok.Getter;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

@Getter
public class KeyCertificatePair {
    private final PrivateKey privateKey;
    private final X509Certificate certificate;

    public KeyCertificatePair(PrivateKey privateKey, X509Certificate certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }

}
