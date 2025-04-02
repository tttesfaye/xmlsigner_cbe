package org.ips.xml.signer.xmlsigner.models;

import lombok.Data;

import java.math.BigInteger;

@Data
public class JWTInfo {


    private String participantBic;
    private String issuer;
    private BigInteger serialNumber;
    private String jit;
    private String expirationTime;
    private String jwt;


}
