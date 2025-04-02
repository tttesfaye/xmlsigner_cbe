package org.ips.xml.signer.xmlsigner.controller;


import org.ips.xml.signer.xmlsigner.service.digestService.XMLDigestVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageDigestVerificationController {

    private XMLDigestVerifier digestVerifier;

    @Autowired
    public MessageDigestVerificationController(XMLDigestVerifier digestVerifier) {
        this.digestVerifier = digestVerifier;
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_XML_VALUE)
    public String verifyXml(@RequestBody String request) {

        String xmlResponse = digestVerifier.verify(request);
        xmlResponse = xmlResponse.replace("&#xD;", "");
        return xmlResponse;
    }


    @PostMapping(value = "/evictCache")
    public String evictCach() {

        digestVerifier.clearCache();
        return "evictede succ";

    }
}
