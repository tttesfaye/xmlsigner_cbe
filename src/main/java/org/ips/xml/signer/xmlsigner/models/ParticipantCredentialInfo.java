package org.ips.xml.signer.xmlsigner.models;

import lombok.Data;

@Data
public class ParticipantCredentialInfo {

    private String userName;
    private String password;
    private String grantType;
    private String jwt;
    private String tokenGenerationPath;
}
