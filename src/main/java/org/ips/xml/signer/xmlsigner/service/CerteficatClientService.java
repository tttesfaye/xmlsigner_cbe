package org.ips.xml.signer.xmlsigner.service;


import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.parser.Authorization;
import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;
import org.ips.xml.signer.xmlsigner.models.TokenInfo;
import org.ips.xml.signer.xmlsigner.response.RestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@Setter
public class CerteficatClientService {

    private RestTemplate restTemplate;
    HttpHeaders headers;
    private URI AUTH_SERVER_UPLOAD_URI;
    private URI AUTH_SERVER_TOKEN_URI;


    @Value("${ets.ips.token.url}")
    private String tokenUrl;

    @Value("${ets.ips.certificate.download.url}")
    private String certeficateDownloadUrl;
    @Value("${ets.ips.userName}")
    private String userName;
    @Value("${ets.ips.password}")
    private String password;
    @Value("${ets.ips.signed.jwt}")
    private String signeJWT;

    @Value("${ets.ips.grantType}")
    private String grantType;

    @Value("${ets.ips.certificate.issuer}")
    private String certficateIssuer;
    @Value("${ets.ips.certificate.serialNumber}")
    private String certeficateSerialNubmer;

    public void create() {
        restTemplate = new RestTemplate();
        AUTH_SERVER_UPLOAD_URI = URI.create(certeficateDownloadUrl);
        AUTH_SERVER_TOKEN_URI = URI.create(tokenUrl);
        headers = new HttpHeaders();

    }

    public CerteficateInformation downloadCerteficate(CerteficateInformation certeficateInformation, String validToken) {
        create();
//         ContentType
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        headers.add("Content-type", "application/x-www-form-urlencoded");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + validToken);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,headers);
        ResponseEntity<CerteficateInformation> responseEntity =
                restTemplate.exchange(certeficateDownloadUrl + "?cert_iss=" +
                        certeficateInformation.getCertificateIssuer() + "&&cert_sn="
                        + certeficateInformation.getCertificateSerialNumber(), HttpMethod.GET, httpEntity, CerteficateInformation.class);
        certeficateInformation.setCertificate(responseEntity.getBody().getCertificate());

        System.out.println(responseEntity);

        return certeficateInformation;

    }

    public ResponseEntity<TokenInfo> generateToken() {
        ResponseEntity<TokenInfo> token = null;
        create();
        // ContentType
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("jwt-assertion", signeJWT);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("username", userName);
        requestBody.add("password", password);
        requestBody.add("grant_type", grantType);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestBody, headers);

        token = restTemplate.postForEntity(AUTH_SERVER_TOKEN_URI, httpEntity,
                TokenInfo.class);


        return token;

    }
}
