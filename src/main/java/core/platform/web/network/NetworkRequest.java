package core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkRequest {
    private String method;
    private RequestParams params;

    public String getMethod() {
        return method;
    }

    public RequestParams getParams() {
        return params;
    }

    public boolean isApi(){
        if(getParams() == null || getParams().getRequest() == null) return false;
        return getParams().getType().equalsIgnoreCase("XHR");
    }
}
