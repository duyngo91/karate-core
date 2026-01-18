package core.healing.domain.model;

public class HealingEvent {
    public String elementId;
    public String original;
    public String healed;
    public String method;
    public double score;
    public boolean success;
    public long timestamp;

    public HealingEvent(String elementId, String original, String healed, String method, double score,
                        boolean success, long timestamp) {
        this.elementId = elementId;
        this.original = original;
        this.healed = healed;
        this.method = method;
        this.score = score;
        this.success = success;
        this.timestamp = timestamp;
    }

    public String getElementId() {
        return elementId;
    }

    public String getOriginal() {
        return original;
    }

    public String getHealed() {
        return healed;
    }

    public String getMethod() {
        return method;
    }

    public double getScore() {
        return score;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
