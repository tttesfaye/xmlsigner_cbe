package org.ips.xml.signer.xmlsigner.service;

import org.ips.xml.signer.xmlsigner.models.JWTInfo;
import org.ips.xml.signer.xmlsigner.models.ParticipantCredentialInfo;
import org.ips.xml.signer.xmlsigner.models.TokenInfo;
import org.ips.xml.signer.xmlsigner.service.apiClient.TokenGenerationClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenGenerationManager {


    @Value("${ets.ips.token.url}")
    private String tokenUrl;
    @Value("${ets.ips.grantType}")
    private String grantType;
    @Value("${ets.ips.userName}")
    private String userName;
    @Value("${ets.ips.password}")
    private String password;
    @Value("${ets.ips.signed.jwt}")
    private String signeJWT;
    TokenGenerationClientService service;
    JWTManager jwtManager;

    @Autowired
    public TokenGenerationManager(TokenGenerationClientService service, JWTManager jwtManager) {
        this.service = service;
        this.jwtManager = jwtManager;
    }

    public TokenInfo getToken() {
        ParticipantCredentialInfo credentialInfo = new ParticipantCredentialInfo();
        TokenInfo tokenInfo = null;
        JWTInfo jwtInfo = jwtManager.getJWT();
        if (jwtInfo != null) {
            credentialInfo.setUserName(userName);
            credentialInfo.setPassword(password);
            credentialInfo.setGrantType(grantType);
            credentialInfo.setTokenGenerationPath(tokenUrl);
            credentialInfo.setJwt(jwtInfo.getJwt());
            tokenInfo = service.generateToken(credentialInfo);
        }
        return tokenInfo;
    }
}
