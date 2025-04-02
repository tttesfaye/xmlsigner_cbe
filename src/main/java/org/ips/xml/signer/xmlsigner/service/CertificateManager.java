package org.ips.xml.signer.xmlsigner.service;

import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;
import org.ips.xml.signer.xmlsigner.models.TokenInfo;
import org.ips.xml.signer.xmlsigner.repository.CacheRepository;
import org.ips.xml.signer.xmlsigner.repository.CertificateCacheRepository;
import org.ips.xml.signer.xmlsigner.service.apiClient.CerteficatClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Optional;

@Service
public class CertificateManager {
    private static final Logger logger = LoggerFactory.getLogger(CertificateManager.class);
    private final CacheRepository cacheRepository;
    private final CertificateCacheRepository certificateCacheRepository;
    @Value("${ets.ips.certificate.download.url}")
    private String certeficateDownloadUrl;
    private TokenGenerationManager tokenGenerationManager;
    private CerteficatClientService certeficatClientService;

    public CertificateManager(CacheRepository cacheRepository, CertificateCacheRepository certificateCacheRepository,
                              TokenGenerationManager tokenGenerationManager,
                              CerteficatClientService certeficatClientService) {
        this.cacheRepository = cacheRepository;
        this.certificateCacheRepository = certificateCacheRepository;
        this.tokenGenerationManager = tokenGenerationManager;
        this.certeficatClientService = certeficatClientService;
    }


    @CacheEvict(value = "certificates", allEntries = true)
    public void clearAllCache() {
        System.out.println("clearing all catche");
    }


    public CerteficateInformation getCertificate(CerteficateInformation certeficateInformation) {

        CerteficateInformation cachedCeretficate = this.getFromCache(certeficateInformation.getCertificateSerialNumber());
        TokenInfo tokenInfo = null;
        if (cachedCeretficate == null || !StringUtils.hasText(cachedCeretficate.getCertificate())) {
            logger.info("calling the certeficate api");
            tokenInfo = tokenGenerationManager.getToken();
            certeficateInformation.setValidToken(tokenInfo.getAccess_token());
            certeficateInformation.setCerteficateDownloadUrl(this.certeficateDownloadUrl);
            CerteficateInformation cert = this.certeficatClientService.downloadCerteficate(certeficateInformation);
            if (cert != null) {
                certeficateInformation.setCertificate(cert.getCertificate());
                cacheRepository.put(certeficateInformation.getCertificateSerialNumber(), certeficateInformation.getCertificate());
                logger.info(cert.toString());
            }
        } else {
            certeficateInformation.setCertificate(cachedCeretficate.getCertificate());
        }
        return certeficateInformation;

    }
    public RSAPublicKey getPublicKeyForMessageOrginator(CerteficateInformation certeficateInformation) {
        RSAPublicKey publicKey = null;
        CerteficateInformation certeficate = null;
        X509Certificate x509Certificate = null;
        try {
            certeficate = this.getCertificate(certeficateInformation);
            if (certeficate != null) {
                x509Certificate = convertBase64StringToCerteficate(certeficate);
                publicKey = (RSAPublicKey) x509Certificate.getPublicKey();

            }
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }

        return publicKey;

    }
    public CerteficateInformation getFromCache(String serialNumber) {
        Optional<String> s = cacheRepository.get(serialNumber);
        CerteficateInformation certeficateInformation = null;
        if (s.isPresent()) {
            logger.debug("Found the key in cache {} ", s.get());
            certeficateInformation = new CerteficateInformation();
            certeficateInformation.setCertificate(s.get());

        }
        return certeficateInformation;
    }

    public X509Certificate convertBase64StringToCerteficate(CerteficateInformation certeficate) throws CertificateException {
        String certificateString = certeficate.getCertificate();
        X509Certificate certificate = null;
        CertificateFactory cf = null;
        try {
            if (certificateString != null && !certificateString.trim().isEmpty()) {
                certificateString = certificateString.replace("-----BEGIN CERTIFICATE-----", "")
                        .replace("-----END CERTIFICATE-----", ""); // NEED FOR PEM FORMAT CERT STRING
                byte[] certificateData = Base64.getDecoder().decode(certificateString);
                cf = CertificateFactory.getInstance("X509");
                certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificateData));

            }
        } catch (CertificateException e) {
            throw new CertificateException(e);
        }
        return certificate;
    }

}
