package core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDetails {
    private String url;
    private Integer status;
    private String statusText;
    private Map<String, String> headers;
    private Object body;
    private String mimeType;
    private Integer encodedDataLength;
    private Double responseTime;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusText() {
        return statusText;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Integer getEncodedDataLength() {
        return encodedDataLength;
    }


    public Double getResponseTime() {
        return responseTime;
    }

}
