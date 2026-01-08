package core.platform.web.element;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TabInfo {
    private String targetId;
    private String type;
    private String title;
    private String url;
    private boolean attached;
    private boolean canAccessOpener;
    private String browserContextId;
    private String openerId;
    private String openerFrameId;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }

    public boolean isCanAccessOpener() {
        return canAccessOpener;
    }

    public void setCanAccessOpener(boolean canAccessOpener) {
        this.canAccessOpener = canAccessOpener;
    }

    public String getBrowserContextId() {
        return browserContextId;
    }

    public void setBrowserContextId(String browserContextId) {
        this.browserContextId = browserContextId;
    }

    public String getOpenerId() {
        return openerId;
    }

    public void setOpenerId(String openerId) {
        this.openerId = openerId;
    }

    public String getOpenerFrameId() {
        return openerFrameId;
    }

    public void setOpenerFrameId(String openerFrameId) {
        this.openerFrameId = openerFrameId;
    }

}