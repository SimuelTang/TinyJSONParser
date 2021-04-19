package pers.simuel.entity;

import pers.simuel.util.JSONBeautifyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author simuel_tang
 * @Date 2021/4/3
 * @Time 14:44
 */
public class JSONArray {
    List<Object> list;

    {
        list = new ArrayList<>();
    }

    public void add(Object obj) {
        list.add(obj);
    }

    public int size() {
        return list.size();
    }

    public Object get(int i) {
        return list.get(i);
    }


    @Override
    public String toString() {
        return JSONBeautifyUtil.beautify(this);
    }
}
