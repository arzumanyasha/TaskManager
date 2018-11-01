package com.example.arturarzumanyan.taskmanager.networking.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {
    public static JSONObject fromMapToJson(HashMap<String, Object> map) {
        JSONObject jsonObject = new JSONObject();

        for (String key : map.keySet()) {
            try {
                Object obj = map.get(key);
                if (obj instanceof Map) {
                    jsonObject.put(key, fromMapToJson((HashMap) obj));
                } else if (obj instanceof List) {
                    jsonObject.put(key, fromListToJson((List) obj));
                } else {
                    jsonObject.put(key, map.get(key) == null ? JSONObject.NULL : map.get(key));
                }
            } catch (JSONException jsone) {

            }
        }

        return jsonObject;
    }

    private static JSONArray fromListToJson(List<Object> list) {
        JSONArray jsonArray = new JSONArray();

        for (Object obj : list) {
            if (obj instanceof HashMap) {
                jsonArray.put(fromMapToJson((HashMap) obj));
            } else if (obj instanceof List) {
                jsonArray.put(fromListToJson((List) obj));
            } else {
                jsonArray.put(obj);
            }
        }

        return jsonArray;
    }
}
