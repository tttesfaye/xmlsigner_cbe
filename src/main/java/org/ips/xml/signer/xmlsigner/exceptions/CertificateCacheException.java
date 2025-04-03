package org.ips.xml.signer.xmlsigner.exceptions;

public class CertificateCacheException extends RuntimeException {

    /**
     * OtpCache Exception with error message
     *
     * @param errorMessage error message
     */
    public CertificateCacheException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * OtpCache Exception with error message and throwable
     *
     * @param errorMessage error message
     * @param throwable    error
     */
    public CertificateCacheException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

}