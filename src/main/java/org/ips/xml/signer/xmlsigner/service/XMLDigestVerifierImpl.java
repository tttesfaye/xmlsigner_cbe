package org.ips.xml.signer.xmlsigner.service;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.ips.xml.signer.xmlsigner.crypto.CerteficateAndKeysUtility;
import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;
import org.ips.xml.signer.xmlsigner.utils.XMLFileUtility;
import org.ips.xml.signer.xmlsigner.utils.XmlSignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Setter

@Service
public class XMLDigestVerifierImpl implements XMLDigestVerifier {
    private static Logger log = LoggerFactory.getLogger(DigestService.class);
    @Autowired
    XMLFileUtility xmlFileUtility;
    @Autowired
    CerteficateAndKeysUtility certeficateAndKeysUtility;
    @Autowired
    private XmlSignUtil signUtil;

    @Override
    public String verify(String signedXml) {


        Document document = xmlFileUtility.createDocumentFromString(signedXml);

        boolean validDocuemnt = false;

        try {
            CerteficateInformation certeficateInformation = xmlFileUtility.parseCerteficateFromDocument(document);
            validDocuemnt = signUtil.verify(document, certeficateAndKeysUtility.loadCerteficateFromLdpa(certeficateInformation));


        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return String.valueOf(validDocuemnt);
    }
}
