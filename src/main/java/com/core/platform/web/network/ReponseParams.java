package com.core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReponseParams {
    private ResponseDetails response;
    private String type;
    private Double requestId;
    private Double timestamp;

    public ResponseDetails getResponse() {
        return response;
    }

    public void setResponse(ResponseDetails response) {
        this.response = response;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRequestId() {
        return requestId;
    }

    public void setRequestId(Double requestId) {
        this.requestId = requestId;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }
}
