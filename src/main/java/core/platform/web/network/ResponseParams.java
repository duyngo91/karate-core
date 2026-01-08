package core.platform.web.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseParams {
    private Double timestamp;
    private String type;
    private Double requestId;
    private ResponseDetails response;



    public Double getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public ResponseDetails getResponse() {
        return response;
    }

    public Double getRequestId() {
        return requestId;
    }
}
