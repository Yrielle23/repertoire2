package framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonBuilder {
    private Map<String, Object> data = new HashMap<>();

    public JsonBuilder put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first)
                json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");

            Object val = entry.getValue();
            if (val instanceof String) {
                json.append("\"").append(escapeJson(val.toString())).append("\"");
            } else if (val instanceof Number) {
                json.append(val);
            } else if (val instanceof Boolean) {
                json.append(val);
            } else if (val instanceof List) {
                json.append(listToJson((List<?>) val));
            } else {
                json.append("\"").append(escapeJson(val.toString())).append("\"");
            }
            first = false;
        }

        json.append("}");
        return json.toString();
    }

    private String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first)
                json.append(",");
            if (item instanceof String) {
                json.append("\"").append(escapeJson(item.toString())).append("\"");
            } else if (item instanceof Number || item instanceof Boolean) {
                json.append(item);
            } else {
                json.append("\"").append(escapeJson(item.toString())).append("\"");
            }
            first = false;
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}