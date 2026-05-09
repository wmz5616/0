package com.zemcho.guzhe.util.tgy;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class SignUtil {

    /**
     * 验签
     *
     * @param key
     * @param sign 要验证的前面
     * @param data 要验签的参数
     * @return
     */
    public static Boolean verifySign(String key, String sign, Object data) {
        try {
//            Map<String, Object> data = (Map) JSON.parse(toJSONString.toString());
//            String sign = String.valueOf(data.get("sign"));
//            String sign = getSignParam(toJSONString);
            String signValue = generateSignNew(data, key);//复杂类型按ascii字符顺序排序，获取签名字符串
            System.out.println("签名:" + signValue);
            if (!sign.equals(signValue)) {
                System.out.println("验签失败");
                return false;
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }


    /****
     * 获取请求sign
     * 通过Json串获取sign
     * @param jsonParam
     * @return
     */
    public static String getSignParam(String jsonParam) {
        String sign = null;
        Map<String, Object> params = (Map) JSON.parse(jsonParam.toString());
        if (params != null) {
            Object signObj = params.get("sign");
            if (signObj != null && !"".equals(signObj)) {
                sign = signObj.toString();
            }
        }
        return sign;
    }

    /**
     * 加签（所有参数，包括父类子类）
     *
     * @param object
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String generateSignNew(Object object, String key) throws UnsupportedEncodingException {
        StringBuffer signContent = new StringBuffer();

        HashMap<String, Object> map = generateMap(object);

        //按字段名ASCII顺序排序
        List<Map.Entry<String, Object>> mappingList = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
        Collections.sort(mappingList, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> mapping1, Map.Entry<String, Object> mapping2) {
                return mapping1.getKey().compareTo(mapping2.getKey());
            }
        });

        //拼接所有字段
        if (mappingList != null && mappingList.size() > 0) {
            for (int i = 0; i < mappingList.size(); i++) {
                Map.Entry<String, Object> maps = (Map.Entry<String, Object>) mappingList.get(i);
                if (maps.getKey().equals("sign") || map.get(maps.getKey()) == null || "".equals(map.get(maps.getKey()))) {
                    continue;
                }
                signContent.append(maps.getKey() + "=" + map.get(maps.getKey()));
                signContent.append("&");
            }
        }
        signContent.append("key=");
        signContent.append(key);
        System.out.println("签名字符串：" + signContent.toString());
        return DigestUtils.md5Hex(signContent.toString().getBytes("UTF-8")).toUpperCase();
    }

    /**
     * Object提取所有属性转map
     *
     * @param object
     * @return
     */
    private static HashMap<String, Object> generateMap(Object object) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            if (object != null) {
                List<Field> fs = getFields(object);
                for (Field f : fs) {
                    if (f.getName().equals("serialVersionUID")) {
                        continue;
                    }
                    if (f.getName().equals("sign")) {
                        continue;
                    }
                    f.setAccessible(true);
                    if (f.getType().toString().contains("class com.zemcho.haircut")) {//需要替换自己object的包名
                        map.putAll(generateMap(f.get(object)));
                    }
                    if (f.getType().toString().equals("class java.lang.String")) {
                        String value = (String) f.get(object);
                        if (value == null || value.equals("")) {
                            continue;
                        }
                        String name = f.getName();
                        map.put(name, value);
                    }
                    if (f.getType().toString().equals("class java.lang.Integer")) {
                        Integer num = (Integer) f.get(object);
                        if (num == null) {
                            continue;
                        }
                        String name = f.getName();
                        map.put(name, num);
                    }
                    if (f.getType().toString().equals("class java.lang.Long")) {
                        Long num = (Long) f.get(object);
                        if (num == null) {
                            continue;
                        }
                        String name = f.getName();
                        map.put(name, num);
                    }
                    if (f.getType().toString().equals("class java.lang.Double")) {
                        Double num = (Double) f.get(object);
                        if (num == null) {
                            continue;
                        }
                        String name = f.getName();
                        map.put(name, num);
                    }
                    if (f.getType().toString().equals("class java.math.BigDecimal")) {
                        BigDecimal inte = (BigDecimal) f.get(object);
                        if (inte == null) {
                            continue;
                        }
                        String name = f.getName();
                        map.put(name, inte);
                    }

                }
            }

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取本类和父类的所有属性
     *
     * @param t
     * @param <T>
     * @return
     */
    private static <T> List<Field> getFields(T t) {
        List<Field> fields = new ArrayList<>();
        Class clazz = t.getClass();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
