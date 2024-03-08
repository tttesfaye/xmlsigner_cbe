package org.ips.xml.signer.xmlsigner.messages;


import lombok.Data;

@Data
public class MultimediaMessage { private String code;
    private String[] params = new String[0];

    public MultimediaMessage(String code) {
        this.code = code;
    }

    public MultimediaMessage(String code, String... params) {
        this.code = code;
        this.params = params;
    }

    @Override
    public String toString() {
        if (params.length == 0) return code;

        StringBuilder messageBuilder = new StringBuilder("code: " + code + ", params: { ");
        for (String param : params) {
            messageBuilder.append("; ").append(param);
        }
        messageBuilder.append(" }");
        return messageBuilder.toString().replaceFirst("; ", "");
    }
}