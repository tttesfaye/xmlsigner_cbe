package org.ips.xml.signer.xmlsigner.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ips.xml.signer.xmlsigner.service.DigestService;
import org.ips.xml.signer.xmlsigner.service.XMLDigestVerifier;
import org.ips.xml.signer.xmlsigner.utils.JwtSigningUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RequestMapping("/api")
@RestController()
public class DigestController {

    @Autowired
    private DigestService digestService;
    @Autowired
    private XMLDigestVerifier digestVerifier;
    @Autowired
    private JwtSigningUtils jwtSigningUtils;

    @PostMapping(value = "/digest", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String handleXmlRequest(@RequestBody String request) {

        String xmlResponse = digestService.signDocument(request);
        xmlResponse = xmlResponse.replace("&#xD;", "");
        return xmlResponse;
    }
    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_XML_VALUE)
    public String verifyXml(@RequestBody String request) {

        String xmlResponse = digestVerifier.verify(request);
        xmlResponse = xmlResponse.replace("&#xD;", "");
        return xmlResponse;
    }
    @PostMapping(value = "/jwt")
    public String getJwt(@RequestBody String request) {

        String xmlResponse = null;
        try {
            xmlResponse = jwtSigningUtils.generateAccessToken(request);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        xmlResponse = xmlResponse.replace("&#xD;", "");
        return xmlResponse;
    }
}
