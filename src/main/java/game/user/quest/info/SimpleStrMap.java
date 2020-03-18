package game.user.quest.info;

import util.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MechAviv on 1/19/2020.
 */
public class SimpleStrMap {
    private final Map<String, String> values;

    public SimpleStrMap() {
        this.values = new HashMap<>();
    }

    public void initFromRawString(String strRaw) {
        values.clear();
        String[] splitted = strRaw.split(";");
        for (String split : splitted) {
            int splitIndex = split.indexOf("=");
            if (splitIndex < 0) {
                continue;
            }
            Logger.logReport("%s=%s", split.substring(0, splitIndex), split.substring(splitIndex + 1));
            values.put(split.substring(0, splitIndex), split.substring(splitIndex + 1));
        }
    }

    public String getValue(String key) {
        return values.getOrDefault(key, "");
    }

    public String getRawString() {
        String result = "";
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (result.isEmpty()) {
                result = String.format("%s=%s", entry.getKey(), entry.getValue());
            } else {
                result = String.format("%s;%s=%s", result, entry.getKey(), entry.getValue());
            }

        }
        return result;
    }

    public boolean setValue(String key, String value) {
        if (key == null || key.length() <= 0) {
            return false;
        }
        if (value != null && value.length() > 0) {
            values.put(key, value);
        } else {
            values.remove(key);
        }
        return true;
    }
}
