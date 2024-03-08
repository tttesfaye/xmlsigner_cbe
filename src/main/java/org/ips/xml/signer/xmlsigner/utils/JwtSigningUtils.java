package org.ips.xml.signer.xmlsigner.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.ips.xml.signer.xmlsigner.crypto.CerteficateAndKeysUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.util.Date;


@Service
public class JwtSigningUtils {
    @Value("${security.pki.privatekey.file.location}")
    private String userName;

    @Value("${security.pki.publickey.file.location}")
    private String password;
    @Value("${ips.participant.bic}")
    private String participantBic;
    @Autowired
    CerteficateAndKeysUtility certeficateAndKeysUtility;
    int accessExpirationMs = 9600000;

    public String generateAccessToken(String userName) throws NoSuchAlgorithmException,Exception {
        PrivateKey privateKey = certeficateAndKeysUtility.loadPrivateKey();
        Algorithm algorithm = Algorithm.RSA256((RSAKey) privateKey);
        X509Certificate key = certeficateAndKeysUtility.getStoredCerteficate();
        BigInteger serialNumber = key.getSerialNumber();
        String issuer = key.getIssuerX500Principal().getName();
        String jwtToken = JWT.create()
                .withIssuer(participantBic)
                .withClaim("cert_iss", issuer)
                .withClaim("cert_sn",String.valueOf(serialNumber))
                .withExpiresAt(new Date(System.currentTimeMillis() + 5000L))
                .withJWTId("11223312412321")
                .sign(algorithm);




        return jwtToken;
    }

}
