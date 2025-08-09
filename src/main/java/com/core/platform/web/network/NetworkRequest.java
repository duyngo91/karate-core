package com.core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkRequest {
    private String method;
    private RequestParams params;

    public boolean isXHR() {
        if (getParams() == null || getParams().getRequest() == null) return false;
        return getParams().getType().equalsIgnoreCase("XHR");
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RequestParams getParams() {
        return params;
    }

    public void setParams(RequestParams params) {
        this.params = params;
    }
}
