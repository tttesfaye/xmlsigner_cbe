package org.ips.xml.signer.xmlsigner.service.apiClient;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Service
@EnableAsync
public class CallBackApiClientService {

    Logger logger = LoggerFactory.getLogger(CallBackApiClientService.class);
    private RestTemplate restTemplate;
    HttpHeaders headers;




    private String merchantCallBackApiPath;


    @Autowired
    public CallBackApiClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }

    public void create() {

        headers = new HttpHeaders();
    }


}
