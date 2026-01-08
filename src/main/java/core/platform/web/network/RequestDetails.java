package core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDetails {
    private Map<String, String> headers;
    private String method;
    private String url;
    private Object postData;

    public Object getPostData() {
        return postData;
    }

    public void setPostData(Object postData) {
        this.postData = postData;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }


}
