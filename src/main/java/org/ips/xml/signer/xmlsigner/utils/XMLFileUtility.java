package org.ips.xml.signer.xmlsigner.utils;


import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.ips.xml.signer.xmlsigner.crypto.CerteficateAndKeysUtility;
import org.ips.xml.signer.xmlsigner.info.ReferenceSignInfo;
import org.ips.xml.signer.xmlsigner.info.SignatureInfo;
import org.ips.xml.signer.xmlsigner.info.SignatureKeyInfo;
import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;
import org.ips.xml.signer.xmlsigner.repository.CertificateCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.security.PrivateKey;
import java.util.Optional;


@Component
@Slf4j
public class XMLFileUtility {
    @Autowired
    private CertificateCacheRepository cacheRepository;

    public ReferenceSignInfo buildDocumentReferenceSignInfo() {
        return ReferenceSignInfo.builder().
                transformAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#").
                digestMethodAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256").
                build();
    }

    public ReferenceSignInfo buildAppHdrReferenceSignInfo() {
        return ReferenceSignInfo.builder().
                transformAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#").
                digestMethodAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256").
                build();
    }

    public ReferenceSignInfo buildKeyReferenceSignInfo() {
        return ReferenceSignInfo.builder().
                transformAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#").
                digestMethodAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256").
                build();
    }

    public SignatureInfo buildKeySignatureInfo() {
        SignatureInfo signatureInfo;
        signatureInfo = SignatureInfo.builder().
                signatureMethodAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1").
                signatureCanonicalizationMethodAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#").
                signatureExclusionTransformer("http://www.w3.org/2001/10/xml-exc-c14n#").
                appHdrReferenceSignInfo(buildAppHdrReferenceSignInfo()).
                documentReferenceSignInfo(buildDocumentReferenceSignInfo()).
                keyReferenceSignInfo(buildKeyReferenceSignInfo()).
                build();
        return signatureInfo;

    }

    public SignatureKeyInfo buildSignaturePrivateKeyInfo() {
        Optional<PrivateKey> privateKeyOpt=this.cacheRepository.getBankPrivatekey();
        PrivateKey privateKey;
        privateKey = privateKeyOpt.orElse(null);
        return SignatureKeyInfo.builder().privateKey(privateKey).build();
    }


    public Document createDocumentFromString(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public CerteficateInformation parseCerteficateFromDocument(Document doc) {
        Node serianNameNode = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509IssuerName").item(0);
        Node serianNumberNode = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509SerialNumber").item(0);
        String serialName = serianNameNode.getTextContent();
        String serialNumber = serianNumberNode.getTextContent();
        CerteficateInformation certeficateInformation = new CerteficateInformation();
        certeficateInformation.setCertificateIssuer(serialName);
        certeficateInformation.setCertificateSerialNumber(serialNumber);
        return certeficateInformation;
    }

}
