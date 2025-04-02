package org.ips.xml.signer.xmlsigner.service.digestService;

public interface XMLDigestVerifier {

    public String verify(String signedXml);

    void clearCache();
}
