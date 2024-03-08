package org.ips.xml.signer.xmlsigner.exceptions;

import lombok.Getter;
import org.ips.xml.signer.xmlsigner.messages.MultimediaMessage;


/**
 * This is the base exception class for all application level exception. It provides ways to create custom exception
 * with externalised messages and parameters.
 */
public class DataException extends RuntimeException {

    @Getter
    private MultimediaMessage openLmisMessage;

    public DataException(String code) {
        openLmisMessage = new MultimediaMessage(code);
    }

    public DataException(String code, Object... params) {
        StringBuilder stringParams = new StringBuilder();
        for (Object param : params) {
            stringParams.append(param.toString()).append("#");
        }
        openLmisMessage = new MultimediaMessage(code, stringParams.toString().split("#"));
    }

    public DataException(MultimediaMessage openLmisMessage) {
        this.openLmisMessage = openLmisMessage;
    }

    @Override
    public String toString() {
        return openLmisMessage.toString();
    }

    @Deprecated
    @Override
    public String getMessage() {
        return openLmisMessage.toString();
    }
}