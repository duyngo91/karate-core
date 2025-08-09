package com.core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestParams {
    private RequestDetails request;
    private String type;
    private Double requestId;

    public RequestDetails getRequest() {
        return request;
    }

    public void setRequest(RequestDetails request) {
        this.request = request;
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
}
