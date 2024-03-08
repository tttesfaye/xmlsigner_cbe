package org.ips.xml.signer.xmlsigner.response;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.ips.xml.signer.xmlsigner.exceptions.DataException;
import org.ips.xml.signer.xmlsigner.messages.MultimediaMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * RestResponse encapsulates ResponseEntity, HttpStatus in order to consistently return responses.
 */

@NoArgsConstructor
@Component
public class RestResponse {
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";





    private Map<String, Object> data = new HashMap<>();

    public RestResponse(String key, Object data) {
        this.data.put(key, data);
    }

    @JsonAnySetter
    public void addData(String key, Object data) {
        this.data.put(key, data);
    }

    public ResponseEntity<RestResponse> response(HttpStatus status) {
        return new ResponseEntity<>(this, status);
    }

    public static ResponseEntity<RestResponse> success(String successMsgCode) {
        return new ResponseEntity<>(new RestResponse(SUCCESS, successMsgCode), HttpStatus.OK);
    }

    public static ResponseEntity<RestResponse> success(MultimediaMessage openLmisMessage) {
        return new ResponseEntity<>(new RestResponse(SUCCESS, openLmisMessage), HttpStatus.OK);
    }

    public static ResponseEntity<RestResponse> error(MultimediaMessage openLmisMessage, HttpStatus statusCode) {
        return new ResponseEntity<>(new RestResponse(ERROR, openLmisMessage), statusCode);
    }

    public static ResponseEntity<RestResponse> error(String errorMsgCode, HttpStatus statusCode) {
        return new ResponseEntity<>(new RestResponse(ERROR, errorMsgCode), statusCode);
    }

    public static ResponseEntity<RestResponse> error(DataException exception, HttpStatus httpStatus) {
        return new ResponseEntity<>(new RestResponse(ERROR, exception.getOpenLmisMessage().getCode()), httpStatus);
    }

    public static ResponseEntity<RestResponse> response(String key, Object value) {
        return new ResponseEntity<>(new RestResponse(key, value), HttpStatus.OK);
    }

    public static ResponseEntity<RestResponse> response(String key, Object value, HttpStatus status) {
        return new ResponseEntity<>(new RestResponse(key, value), status);
    }

    public static ResponseEntity<RestResponse> response(Map<String, MultimediaMessage> messages, HttpStatus status) {
        RestResponse response = new RestResponse();
        response.setData(messages);
        return new ResponseEntity<>(response, status);
    }

    @JsonAnyGetter
    @SuppressWarnings("unused")
    public Map<String, Object> getData() {
        return data;
    }

    private void setData(Map<String, MultimediaMessage> errors) {
        for (String key : errors.keySet()) {
            addData(key, errors.get(key));
        }
    }

    @JsonIgnore
    public String getError() {
        return (String) data.get(ERROR);
    }

    @JsonIgnore
    public String getSuccess() {
        return (String) data.get(SUCCESS);
    }

}