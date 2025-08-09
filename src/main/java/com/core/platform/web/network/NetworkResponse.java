package com.core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkResponse {
    private String method;
    private ReponseParams params;

    public boolean isXHR() {
        if (getParams() == null || getParams().getResponse() == null) return false;
        return getParams().getType().equalsIgnoreCase("XHR");
    }

    public String getMethod() {
        return method;
    }



    public ReponseParams getParams() {
        return params;
    }

}
