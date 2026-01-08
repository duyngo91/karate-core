package core.mcp.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SchemaLoader {
    private static final Map<String, String> schemas = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();
    
    static {
        loadSchemas();
    }
    
    private static void loadSchemas() {
        try {
            InputStream is = SchemaLoader.class.getResourceAsStream("/mcp-tools-schema.json");
            JsonNode root = mapper.readTree(is);
            
            root.fieldNames().forEachRemaining(toolName -> {
                schemas.put(toolName, root.get(toolName).toString());
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load schemas", e);
        }
    }
    
    public static String getSchema(String toolName) {
        return schemas.get(toolName);
    }
}