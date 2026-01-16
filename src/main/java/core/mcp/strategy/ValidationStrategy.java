package core.mcp.strategy;

import java.util.Map;

public interface ValidationStrategy {
    void validate(Map<String, Object> args);
}
