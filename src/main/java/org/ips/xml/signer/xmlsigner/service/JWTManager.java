package org.ips.xml.signer.xmlsigner.service;

import org.ips.xml.signer.xmlsigner.models.JWTInfo;
import org.ips.xml.signer.xmlsigner.utils.JwtSigningUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JWTManager {
    @Value("${ips.participant.bic}")
    private String participantBic;

    private JwtSigningUtils jwtSigningUtils;

    @Autowired
    public JWTManager(JwtSigningUtils jwtSigningUtils) {
        this.jwtSigningUtils = jwtSigningUtils;
    }

    public JWTInfo getJWT() {
        JWTInfo jwtInfo = new JWTInfo();
        try {
            jwtInfo.setParticipantBic(participantBic);
            jwtSigningUtils.generateJwt(jwtInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jwtInfo;
    }
}
