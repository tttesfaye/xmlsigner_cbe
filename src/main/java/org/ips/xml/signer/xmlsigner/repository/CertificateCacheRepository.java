package org.ips.xml.signer.xmlsigner.repository;

import lombok.extern.slf4j.Slf4j;
import org.ips.xml.signer.xmlsigner.exceptions.CertificateCacheException;
import org.ips.xml.signer.xmlsigner.models.KeyCertificatePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.Map;

@Repository
@Slf4j
public class CertificateCacheRepository implements CacheRepository {

    private final long ttl;
    private final String keystorePath;
    private final String keystorePassword;
    private final Map<String, X509Certificate> certificateCache;
    private final ConcurrentHashMap<String, KeyCertificatePair> pairCache;

    private volatile boolean cacheLoaded = false;


    @Value("${security.pki.keystore.type}")
    private String keystoreType;

    @Value("${ips.participant.bic}")
    String bankBic;

    @Autowired
    public CertificateCacheRepository(@Value("${security.pki.keystore.file.location}") String keystorePath,
                                      @Value("${security.pki.keystore.file.store.password}") String keystorePassword,
                                      @Value("${spring.redis.timeToLive}") long ttl) {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.ttl = ttl;
        this.certificateCache = new ConcurrentHashMap<>();
        this.pairCache = new ConcurrentHashMap<>();
    }

    /**
     * Initialize: Load all certificates from the keystore at startup
     */
    @PostConstruct
    public void init() {
        loadCertificatesFromKeystore();
    }

    /**
     * Load certificates from the Java Keystore into the cache
     */
    private void loadCertificatesFromKeystore() {
        try (FileInputStream keystoreStream = new FileInputStream(keystorePath)) {
            KeyStore keystore = KeyStore.getInstance(keystoreType);
            keystore.load(keystoreStream, keystorePassword.toCharArray());
            log.info(" the path to the keystore is :" + keystorePath);
            Enumeration<String> aliases = keystore.aliases();
            log.info("found  key file and there are  " + aliases.toString());
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                log.info("catching certificate for   " + alias);
                Certificate certificate = keystore.getCertificate(alias);
                if (certificate instanceof X509Certificate) {
                    certificateCache.put(alias, (X509Certificate) certificate);
                }
            }

            // Retrieve the private key
            Key bankPrivateKey = keystore.getKey(bankBic.toLowerCase(), keystorePassword.toCharArray());
            X509Certificate bankCertificate = certificateCache.get(bankBic.toLowerCase());
            if (bankPrivateKey instanceof PrivateKey) {
                PrivateKey privateKey = (PrivateKey) bankPrivateKey;
                pairCache.put("keys", new KeyCertificatePair(privateKey, bankCertificate));
                log.info("catching pair of keys for   " + bankBic);
                // Further processing of the private key
            } else {
                log.info(" instance is " +bankPrivateKey!=null? bankPrivateKey.getClass().getCanonicalName():"");
                log.info("No private key found with the alias: " + bankBic);
            }
            cacheLoaded = true;
        } catch (Exception e) {
            cacheLoaded = false;
            log.error("Error while loading certificates from keystore", e);
            throw new CertificateCacheException("Error while loading certificates from keystore", e);

        }
    }

    /**
     * Returns the cached certificate for the given alias
     *
     * @param alias certificate alias
     * @return cached certificate
     */
    public Optional<X509Certificate> get(String alias) {
        if (!cacheLoaded) {
            refreshCache();
        } else {
            log.info(" the file is already loaded ....... trying to bet the bank private key and the catched size is " + certificateCache.size());
        }
        return Optional.ofNullable(certificateCache.get(alias));
    }

    /**
     * Save a new certificate to the cache and keystore
     *
     * @param alias       certificate alias
     * @param certificate certificate to add
     */
    public void put(String alias, X509Certificate certificate) {
        try {
            // Add to the cache
            certificateCache.put(alias, certificate);

            // Optionally, import to the keystore
            importCertificateToKeystore(alias, certificate);
        } catch (Exception e) {
            throw new CertificateCacheException("Error while adding certificate to cache", e);
        }
    }

    /**
     * Remove a certificate from the cache
     *
     * @param alias certificate alias
     */
    public void remove(String alias) {
        certificateCache.remove(alias);
    }

    /**
     * Import a new certificate to the keystore
     */
    public void importCertificateToKeystore(String alias, X509Certificate certificate) throws Exception {
        try (FileInputStream keystoreStream = new FileInputStream(keystorePath)) {
            KeyStore keystore = KeyStore.getInstance(keystoreType);
            keystore.load(keystoreStream, keystorePassword.toCharArray());

            keystore.setCertificateEntry(alias, certificate);

            try (FileOutputStream keystoreOutStream = new FileOutputStream(keystorePath)) {
                keystore.store(keystoreOutStream, keystorePassword.toCharArray());
            }
        } catch (IOException e) {
            throw new CertificateCacheException("Error while importing certificate to keystore", e);
        }
    }

    /**
     * Scheduled task to refresh the cache at fixed intervals
     */
    @Scheduled(fixedRateString = "${certificate.cache.refreshInterval:10000}")
    public void refreshCache() {
        log.debug(" trying to refresh the cache");
        loadCertificatesFromKeystore();
    }

    /**
     * Custom exception for certificate cache errors
     */
    public static class CertificateCacheException extends RuntimeException {
        public CertificateCacheException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public Optional<PrivateKey> getBankPrivatekey() {
        if (!cacheLoaded) {
            refreshCache();
        } else {
            log.info(" the file is already loaded ....... trying to bet the bank private key and the catched size is " + certificateCache.size());
        }
        return Optional.ofNullable(this.pairCache.get("keys").getPrivateKey());
    }

    public Optional<X509Certificate> getBankCertificate() {
        if (!cacheLoaded) {
            refreshCache();
        }
        return Optional.ofNullable(this.pairCache.get("keys").getCertificate());
    }
}
