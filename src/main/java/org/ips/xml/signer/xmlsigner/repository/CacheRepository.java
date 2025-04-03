package org.ips.xml.signer.xmlsigner.repository;

import org.springframework.scheduling.annotation.Scheduled;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Optional;

/**
 * CacheRepository
 */
public interface CacheRepository {

    public Optional<X509Certificate> get(String alias);

    /**
     * Save a new certificate to the cache and keystore
     *
     * @param alias certificate alias
     * @param certificate certificate to add
     */
    public void put(String alias, X509Certificate certificate) ;

    /**
     * Remove a certificate from the cache
     *
     * @param alias certificate alias
     */
    public void remove(String alias) ;

    /**
     * Import a new certificate to the keystore
     */
    public void importCertificateToKeystore(String alias, X509Certificate certificate) throws Exception;

    /**
     * Scheduled task to refresh the cache at fixed intervals
     */

    public void refreshCache() ;


}