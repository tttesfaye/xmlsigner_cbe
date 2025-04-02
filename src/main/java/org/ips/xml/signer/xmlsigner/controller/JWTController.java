package org.ips.xml.signer.xmlsigner.controller;

import org.ips.xml.signer.xmlsigner.models.JWTInfo;
import org.ips.xml.signer.xmlsigner.response.RestResponse;
import org.ips.xml.signer.xmlsigner.service.JWTManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class JWTController {


    private JWTManager jwtManager;

    @Autowired
    public JWTController(JWTManager jwtManager) {

        this.jwtManager = jwtManager;
    }


    @PostMapping(value = "/jwt")
    public ResponseEntity<RestResponse> getJwt(@RequestBody String userName) {

        ResponseEntity<RestResponse> xmlResponse = null;
        JWTInfo jwtInfo = null;
        try {
            jwtInfo = jwtManager.getJWT();
            xmlResponse = RestResponse.success("success");
            xmlResponse.getBody().addData("jwt", jwtInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return xmlResponse;
    }

}
