package core.platform.web.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.karate.Json;
import com.intuit.karate.StringUtils;
import com.intuit.karate.driver.DevToolsDriver;
import com.intuit.karate.driver.DevToolsMessage;
import com.intuit.karate.driver.DriverOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageHandler {
    protected final DriverOptions options;
    protected final DevToolsDriver driver;
    public final List<NetworkResponse> responses = new ArrayList<>();
    public final List<NetworkRequest> requests = new ArrayList<>();
    public static final String METHOD = "method";
    public static final String NETWORK_REQUEST_WILL_BE_SENT = "Network.requestWillBeSent";
    public static final String NETWORK_RESPONSE_RECEIVED = "Network.responseReceived";
    public static final String NETWORK_RESPONSE_BODY = "Network.getResponseBody";
    public static final String NETWORK_REQUEST_BODY = "Network.getRequestPostData";
    public static final String BODY = "body";
    public static final String POST_DATA = "postData";

    public MessageHandler(DriverOptions options, DevToolsDriver driver) {
        this.options = options;
        this.driver = driver;
    }


    public void addRequest(Map<String, Object> map) {
        if (!map.isEmpty() && (map.containsKey(METHOD))) {
            ObjectMapper mapper = new ObjectMapper();
            try {

                if (map.get(METHOD).equals(NETWORK_REQUEST_WILL_BE_SENT)) {
                    NetworkRequest request = mapper.convertValue(map, NetworkRequest.class);
                    if (request.isApi()) {
                        requests.add(request);
                    }
                }

                if (map.get(METHOD).equals(NETWORK_RESPONSE_RECEIVED)) {
                    NetworkResponse response = mapper.convertValue(map, NetworkResponse.class);
                    if (response.isApi()) {
                        responses.add(response);
                    }
                }

            } catch (IllegalArgumentException e) {
                options.driverLogger.error("Lỗi khi parse dữ liệu : " + e.getMessage());
            }
        }
    }

    public Map<String, Object> getAPIBody(String requestId, String method) {
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("requestId", requestId);
        DevToolsMessage dtm = new DevToolsMessage(driver, method);
        dtm.params(bodyParams);
        DevToolsMessage msg = dtm.send();

        return msg.getResult().getValue();
    }

    public Object getResponseBody(String requestId) {
        Map<String, Object>result = getAPIBody(requestId, NETWORK_RESPONSE_BODY);
        return result != null && result.containsKey(BODY) ? result.get(BODY) : result;
    }

    public Object getRequestBody(String requestId) {
        Map<String, Object> result = getAPIBody(requestId, NETWORK_REQUEST_BODY);
        return result != null && result.containsKey(POST_DATA) ? result.get(POST_DATA) : result;
    }

    public Function<String, Boolean> createTextHandler() {
        return text -> {
            if (options.driverLogger.isTraceEnabled()) {
                options.driverLogger.trace("<< {}", text);
            } else {
                options.driverLogger.debug("<< {}", StringUtils.truncate(text, 1024, true));
            }
            Map<String, Object> map = Json.of(text).value();

            DevToolsMessage dtm = new DevToolsMessage(driver, map);
            driver.receive(dtm);
            addRequest(map);
            return false;
        };
    }

    public List<NetworkResponse> getResponses() {
        return responses;
    }

    public List<NetworkRequest> getRequests() {
        return requests;
    }

    public Object getResponseAPI(String url) {
        return Json.of(responses.stream()
                .filter(r -> r.getParams().getResponse().getUrl().contains(url))
                .map(x -> {
                    if (x.getParams().getResponse().getBody() == null) {
                        Object result = getResponseBody(x.getParams().getRequestId().toString());
                        if (result != null) {
                            Json data = Json.of(getResponseBody(x.getParams().getRequestId().toString()));
                            if (data.isArray()) {
                                x.getParams().getResponse().setBody(data.asList());
                            } else {
                                x.getParams().getResponse().setBody(
                                        data.value()
                                );
                            }
                        }
                    }
                    return Json.of(x).value();
                }).collect(Collectors.toList())).value();
    }

    public Object getRequestAPI(String url) {
        return Json.of(requests.stream()
                .filter(r -> r.getParams().getRequest().getUrl().contains(url))
                .map(x -> {
                    if (x.getParams().getRequest().getPostData() == null) {
                        Object result = getRequestBody(x.getParams().getRequestId().toString());
                        if (result != null) {
                            Json data = Json.of(getRequestBody(x.getParams().getRequestId().toString()));
                            if (data.isArray()) {
                                x.getParams().getRequest().setPostData(data.asList());
                            } else {
                                x.getParams().getRequest().setPostData(
                                        data.value()
                                );
                            }
                        }
                    }
                    return Json.of(x).value();
                }).collect(Collectors.toList())).value();
    }
}
