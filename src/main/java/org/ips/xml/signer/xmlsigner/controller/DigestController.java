package org.ips.xml.signer.xmlsigner.controller;

import org.ips.xml.signer.xmlsigner.service.digestService.DigestService;
import org.ips.xml.signer.xmlsigner.service.digestService.XMLDigestVerifier;
import org.ips.xml.signer.xmlsigner.utils.JwtSigningUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RequestMapping("/api")
@RestController()
public class DigestController {


    private DigestService digestService;







    @Autowired
    DigestController(
            DigestService digestService,
            XMLDigestVerifier digestVerifier,
            JwtSigningUtils jwtSigningUtils) {
        this.digestService = digestService;

    }

    @PostMapping(value = "/digest", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String handleXmlRequest(@RequestBody String request) {

        String xmlResponse = digestService.signDocument(request);
        xmlResponse = xmlResponse.replace("&#xD;", "");
        return xmlResponse;
    }


}



