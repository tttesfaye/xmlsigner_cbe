package org.ips.xml.signer.xmlsigner.service;


import lombok.extern.log4j.Log4j2;
import org.ips.xml.signer.xmlsigner.info.SignatureInfo;
import org.ips.xml.signer.xmlsigner.info.SignatureKeyInfo;
import org.ips.xml.signer.xmlsigner.utils.XMLFileUtility;
import org.ips.xml.signer.xmlsigner.utils.XmlSignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ips.xml.signer.xmlsigner.context.Constants.BAH_NAME;
import static org.ips.xml.signer.xmlsigner.context.Constants.WS_SECURITY_NAME;


@Service
public class DigestServiceImpl implements DigestService {
    public static String xadesNS = "http://uri.etsi.org/01903/v1.3.2#";
    public static String signatureID = "Sig1";
    public static String signedPropID = "SignP";
private static Logger log = LoggerFactory.getLogger(DigestService.class);
    @Autowired
    private XMLFileUtility xmlFileUtility;
    @Autowired
    private XmlSignUtil signUtil;

    @Override
    public String signDocument(String xmlString) {

        String signedXml = null;
        Document document = xmlFileUtility.createDocumentFromString(xmlString);
        Document signedDocument=null;
        SignatureInfo signatureInfo = xmlFileUtility.buildKeySignatureInfo();
        SignatureKeyInfo signatureKeyInfo = xmlFileUtility.buildSignaturePrivateKeyInfo();

        try {
            signedDocument= signUtil.sign(document, signatureInfo, signatureKeyInfo);
            signedXml = convertDocumentToString(signedDocument);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return signedXml;
    }

    public static Element createElement(Document doc, String tag, String prefix, String nsURI) {
        String qName = prefix == null ? tag : prefix + ":" + tag;
        return doc.createElementNS(nsURI, qName);
    }

    private String convertDocumentToString(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private Document createDocumentFromString(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
