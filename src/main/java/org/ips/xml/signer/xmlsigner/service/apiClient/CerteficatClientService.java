package org.ips.xml.signer.xmlsigner.service.apiClient;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;

import org.springframework.stereotype.Service;

import org.springframework.util.MultiValueMap;

import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Service
@Setter
@Slf4j
public class CerteficatClientService {
    Logger logger = LoggerFactory.getLogger(CerteficatClientService.class);
    private RestTemplate restTemplate;
    HttpHeaders headers;





    @Autowired
    public CerteficatClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }

    public void create() {

        headers = new HttpHeaders();
    }



    public CerteficateInformation downloadCerteficate(CerteficateInformation certeficateInformation) {
        create();
//         ContentType
        logger.info("calling the certeficate api");
        headers.add("Content-type", "application/x-www-form-urlencoded");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + certeficateInformation.getValidToken());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<CerteficateInformation> responseEntity =
                restTemplate.exchange(certeficateInformation.getCerteficateDownloadUrl() + "?cert_iss=" +
                        certeficateInformation.getCertificateIssuer() + "&&cert_sn="
                        + certeficateInformation.getCertificateSerialNumber(), HttpMethod.GET, httpEntity, CerteficateInformation.class);
        CerteficateInformation cert = responseEntity.getBody();
        if (cert != null) {
            certeficateInformation.setCertificate(cert.getCertificate());
            logger.info(responseEntity.toString());

        }


        return certeficateInformation;

    }



}
