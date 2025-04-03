package org.ips.xml.signer.xmlsigner.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.cert.Certificate;
import org.bouncycastle.openssl.PEMParser;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KeystoreCreator {
    public static void main(String[] args) throws Exception {
        // Load private key
        PrivateKey privateKey = loadPrivateKey("C:/etswitch/xmlSigner_2/xmlSigner/src/main/resources/xml/JWT_Private.key");

        // Load certificate
        Certificate certificate = loadCertificate("C:/etswitch/xmlSigner_2/xmlSigner/src/main/resources/xml/certnew.cer");

        // Create PKCS#12 keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null); // Initialize an empty keystore

        // Set the key entry
        char[] password = "keystorepassword".toCharArray(); // Keystore password
        keyStore.setKeyEntry("CBETETAA", privateKey, password, new Certificate[]{certificate});

        // Save the keystore to a file
        try (FileOutputStream fos = new FileOutputStream("C:/etswitch/national_payment_gateway/certs/keys/participants/wegagen/keystore1.p12")) {
            keyStore.store(fos, password);
        }
    }

    private static PrivateKey loadPrivateKey(String path) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static Certificate loadCertificate(String path) throws Exception {
        try (FileInputStream fis = new FileInputStream(path)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return cf.generateCertificate(fis);
        }
    }
}
