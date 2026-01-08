package core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkResponse {
    private String method;
    private ResponseParams params;

    public String getMethod() {
        return method;
    }

    public ResponseParams getParams() {
        return params;
    }

    public boolean isApi(){
        if(getParams() == null || getParams().getResponse() == null) return false;
        return getParams().getType().equalsIgnoreCase("XHR");
    }

}
