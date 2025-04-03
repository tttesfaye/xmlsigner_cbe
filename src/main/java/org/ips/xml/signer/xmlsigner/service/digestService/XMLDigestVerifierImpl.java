package org.ips.xml.signer.xmlsigner.service.digestService;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;
import org.ips.xml.signer.xmlsigner.service.CertificateManager;
import org.ips.xml.signer.xmlsigner.utils.XMLFileUtility;
import org.ips.xml.signer.xmlsigner.utils.XmlSignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Setter
@Slf4j
@Service
@NoArgsConstructor
public class XMLDigestVerifierImpl implements XMLDigestVerifier {

    XMLFileUtility xmlFileUtility;

    CertificateManager certificateManager;

    private XmlSignUtil signUtil;

    @Autowired
    public XMLDigestVerifierImpl(XMLFileUtility xmlFileUtility,  CertificateManager certificateManager, XmlSignUtil signUtil) {
        this.xmlFileUtility = xmlFileUtility;
        this.certificateManager = certificateManager;
        this.signUtil = signUtil;
    }

    @Override
    public String verify(String signedXml) {

        Document document = xmlFileUtility.createDocumentFromString(signedXml);

        boolean validDocuemnt = false;

        try {
            CerteficateInformation certeficateInformation = xmlFileUtility.parseCerteficateFromDocument(document);
            validDocuemnt = signUtil.verify(document,certificateManager.getPublicKeyForMessageOrginator(certeficateInformation));


        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return String.valueOf(validDocuemnt);
    }

    @Override
    public void clearCache() {
        certificateManager.clearAllCache();
    }
}
