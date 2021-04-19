package pers.simuel.util;

import pers.simuel.entity.JSONArray;
import pers.simuel.entity.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @Author simuel_tang
 * @Date 2021/4/4
 * @Time 10:39
 */
public class JSONBeautifyUtil {

    private static final char SPACE_CHAR = ' ';
    private static final int INDENT_WIDTH = 2;
    private static int callDepth = 0;

    /**
     * @param jsonObject
     * @return
     */
    public static String beautify(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndentString());
        sb.append("{");
        callDepth++;
        List<Map.Entry<String, Object>> keyValues = jsonObject.getAllPairs();
        for (int i = 0; i < keyValues.size(); i++) {
            // 获取键值对
            Map.Entry<String, Object> keyValue = keyValues.get(i);
            String key = keyValue.getKey();
            Object value = keyValue.getValue();

            // 先添加key信息
            // 步骤：
            // 1.进入循环，说明这个对象有键值对的存在，所以按照格式，先换行、缩进再添加key。
            // 2.因为解析JSONObject后，冒号和引号都不存在，所以由我们自行添加。
            // 3.根据value的类型，判断解析方式
            sb.append("\n");
            sb.append(getIndentString());
            sb.append("\"");
            sb.append(key);
            sb.append("\"");
            sb.append(":");

            // 对value判断
            if (value instanceof JSONObject) {
                sb.append("\n");
                sb.append(beautify((JSONObject) value));
            } else if (value instanceof JSONArray) {
                sb.append("\n");
                sb.append(beautify((JSONArray) value));
            } else if (value instanceof String) {
                sb.append("\"");
                sb.append(value);
                sb.append("\"");
            } else {
                sb.append(value);
            }

            // 判断是否需要逗号
            if (i < keyValues.size() - 1) {
                sb.append(",");
            }
        }
        callDepth--;
        sb.append("\n");
        sb.append(getIndentString());
        sb.append("}");
        return sb.toString();
    }

    public static String beautify(JSONArray jsonArray) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndentString());
        sb.append("[");
        callDepth++;
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            sb.append("\n");
            Object elem = jsonArray.get(i);
            if (elem instanceof JSONObject) {
                sb.append(beautify((JSONObject) elem));
            } else if (elem instanceof JSONArray) {
                sb.append(beautify((JSONArray) elem));
            } else if (elem instanceof String) {
                sb.append(getIndentString());
                sb.append("\"");
                sb.append(elem);
                sb.append("\"");
            } else {
                sb.append(getIndentString());
                sb.append(elem);
            }
            if (i < size - 1) {
                sb.append(",");
            }
        }
        callDepth--;
        sb.append("\n");
        sb.append(getIndentString());
        sb.append("]");
        return sb.toString();
    }

    private static String getIndentString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < callDepth * INDENT_WIDTH; i++) {
            sb.append(SPACE_CHAR);
        }
        return sb.toString();
    }

}
