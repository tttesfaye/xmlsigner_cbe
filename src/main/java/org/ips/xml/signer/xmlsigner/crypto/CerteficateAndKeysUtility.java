package org.ips.xml.signer.xmlsigner.crypto;

import lombok.Data;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.ips.xml.signer.xmlsigner.info.ReferenceSignInfo;
import org.ips.xml.signer.xmlsigner.models.CerteficateInformation;
import org.ips.xml.signer.xmlsigner.models.TokenInfo;
import org.ips.xml.signer.xmlsigner.service.CerteficatClientService;
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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;


@Component
@Data
public class CerteficateAndKeysUtility {
    @Autowired
    CerteficatClientService certeficatClientService;
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
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(new FileInputStream(keyStoreFile), storePassword.toCharArray());
            this.keyStore = keyStore;
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
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
            throw new RuntimeException(e);
        }


        return privateKey;

    }



    public PrivateKey readPKCS8PrivateKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        FileReader keyReader = new FileReader(file);
        PemReader pemReader = new PemReader(keyReader);
        PemObject pemObject = pemReader.readPemObject();
        byte[] content = pemObject.getContent();
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
        return factory.generatePrivate(privKeySpec);

    }


    public RSAPublicKey readX509PublicKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        FileReader keyReader = new FileReader(file);
        PemReader pemReader = new PemReader(keyReader);
        PemObject pemObject = pemReader.readPemObject();
        byte[] content = pemObject.getContent();
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
        return (RSAPublicKey) factory.generatePublic(pubKeySpec);

    }

    public RSAPublicKey loadCerteficateFromLdpa(CerteficateInformation certeficateInformation) throws Exception {
        ResponseEntity<TokenInfo> tokenInfo = this.certeficatClientService.generateToken();
        TokenInfo token = tokenInfo.getBody();
        CerteficateInformation certeficate = this.certeficatClientService.downloadCerteficate(certeficateInformation, token.getAccess_token());
        X509Certificate x509Certificate = convertBase64StringToCerteficate(certeficate);
        RSAPublicKey publicKey = (RSAPublicKey) x509Certificate.getPublicKey();
        return publicKey;

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
    public X509Certificate getStoredCerteficate() {
        X509Certificate cer=null;
        CertificateFactory keyFactory = null;
        try {
            keyFactory = CertificateFactory.getInstance("x.509");
            FileInputStream is = new FileInputStream(certificateKeyPath);
            cer = (X509Certificate) keyFactory.generateCertificate(is);


        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }  catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return cer;
    }

}
