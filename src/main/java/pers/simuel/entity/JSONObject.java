package pers.simuel.entity;

import pers.simuel.util.JSONBeautifyUtil;

import java.util.*;

/**
 * @Author simuel_tang
 * @Date 2021/4/3
 * @Time 10:35
 */
public class JSONObject {

    private final Map<String, Object> map;

    {
        map = new LinkedHashMap<>();
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public List<Map.Entry<String, Object>> getAllPairs() {
        return new ArrayList<>(map.entrySet());
    }

    @Override
    public String toString() {
        return JSONBeautifyUtil.beautify(this);
    }
}
