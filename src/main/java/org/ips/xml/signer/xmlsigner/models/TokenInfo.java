package org.ips.xml.signer.xmlsigner.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data

public class TokenInfo {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private Long expires_in;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("refresh_expires_in")
    private Long refresh_expires_in;

}
