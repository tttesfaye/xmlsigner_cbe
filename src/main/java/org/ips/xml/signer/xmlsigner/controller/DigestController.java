package org.ips.xml.signer.xmlsigner.controller;

import org.ips.xml.signer.xmlsigner.service.digestService.DigestService;
import org.ips.xml.signer.xmlsigner.service.digestService.XMLDigestVerifier;
import org.ips.xml.signer.xmlsigner.utils.JwtSigningUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        // Sanitize XML input
        if (!isValidXml(request)) {
            return HttpStatus.BAD_REQUEST +"Invalid XML input";
        }

        // Securely parse XML inside digestService.signDocument()
        String xmlResponse = digestService.signDocument(request);

        // Remove unsafe XML characters
        xmlResponse = xmlResponse.replace("&#xD;", "");
        return xmlResponse;
    }

    // Example XML validation function
    private boolean isValidXml(String xml) {
        return xml != null && xml.trim().startsWith("<?xml");
    }

}



