package org.ips.xml.signer.xmlsigner.crypto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;
import org.ips.xml.signer.xmlsigner.models.TokenInfo;
import org.ips.xml.signer.xmlsigner.service.CertificateManager;
import org.ips.xml.signer.xmlsigner.service.apiClient.CerteficatClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Component
@Data
@NoArgsConstructor
public class CerteficateAndKeysUtility {
    private static final Logger logger = LoggerFactory.getLogger(CerteficateAndKeysUtility.class);

    @Value("${security.pki.keystore.file.location}")
    private String keyStoreFile;
    @Value("${security.pki.keystore.file.store.password}")
    private String storePassword;

    @Value("${security.pki.keystore.file.key.password}")
    private String keyPassword;
    @Value("${security.pki.keystore.type}")
    private String keyStoreType;

    @Value("${security.pki.keystore.ets.key.alias}")
    private String etsAliasName;

    @Value("${participant.epg.acs.certificate.alias}")
    private String participantAlias;
    private KeyStore keyStore;
    @Value("${security.pki.privatekey.file.location}")
    private String privateKeyPath;
    @Value("${security.pki.certificate.file.location}")
    private String certificateKeyPath;



    private void loadKeyStore() {
        KeyStore keyStoreLoc = null;

        try {
            keyStoreLoc = KeyStore.getInstance(keyStoreType);

            keyStoreLoc.load(new FileInputStream(keyStoreFile), storePassword.toCharArray());
            this.keyStore = keyStoreLoc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public PrivateKey getStoredPrivateKey(String filePath) {
        PrivateKey privateKey = null;
        try {
            this.loadKeyStore();
            privateKey = (PrivateKey) this.keyStore.getKey(participantAlias, keyPassword.toCharArray());
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
        return privateKey;
    }


    public PublicKey getStoredPublicKey(String aliasName) {
        PublicKey publicKey = null;
        try {

            Certificate certificate;
            this.loadKeyStore();
            certificate = this.keyStore.getCertificate(aliasName);
            publicKey = certificate.getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        return publicKey;
    }

    public PrivateKey loadPrivateKey() {
        PrivateKey privateKey = null;

        File file = new File(privateKeyPath);

        try {
            privateKey = readPKCS8PrivateKey(file);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }


        return privateKey;

    }


    public PrivateKey readPKCS8PrivateKey(File file) {
        PemReader pemReader = null;
        FileReader keyReader = null;
        KeyFactory factory = null;
        PKCS8EncodedKeySpec privKeySpec = null;
        PrivateKey privateKey = null;
        try {
            factory = KeyFactory.getInstance("RSA");
            keyReader = new FileReader(file);
            pemReader = new PemReader(keyReader);
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            privKeySpec = new PKCS8EncodedKeySpec(content);
            privateKey = factory.generatePrivate(privKeySpec);


        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (Exception e) {

            logger.error(e.getMessage());
        } finally {
            if (keyReader != null) {
                try {
                    keyReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }

            }
            if (pemReader != null) {
                try {
                    pemReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }

            }
        }
        return privateKey;

    }


    public RSAPublicKey readX509PublicKey(File file) {
        KeyFactory factory = null;
        FileReader keyReader = null;
        PemObject pemObject = null;
        PemReader pemReader = null;
        RSAPublicKey publicKey = null;
        try {
            factory = KeyFactory.getInstance("RSA");

            keyReader = new FileReader(file);
            pemReader = new PemReader(keyReader);
            pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            publicKey = (RSAPublicKey) factory.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage());
        } finally {
            if (keyReader != null) {
                try {
                    keyReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (pemReader != null) {
                try {
                    pemReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return publicKey;
    }




    public X509Certificate getStoredCerteficate() {
        X509Certificate cer = null;
        CertificateFactory keyFactory = null;
        try {
            keyFactory = CertificateFactory.getInstance("x.509");
            FileInputStream is = new FileInputStream(certificateKeyPath);
            cer = (X509Certificate) keyFactory.generateCertificate(is);


        } catch (NullPointerException npe) {
            logger.error(npe.getMessage());
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return cer;
    }


}
