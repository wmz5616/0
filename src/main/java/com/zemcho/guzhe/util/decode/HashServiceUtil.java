package com.zemcho.guzhe.util.decode;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.lang.util.ByteSource;

public class HashServiceUtil {

    public static final String HASH_ALGORITHM_NAME = "md5";
    public static final int HASH_ITERATIONS = 16;
    public static final String SALT = "zemcho@java!Ztetws@5983";

    public static String computeHash(String text){

        return computeHash(text,SALT);
    }

    public static String computeHash(String text, String salt){

        return computeHash(text,salt,HASH_ALGORITHM_NAME);
    }

    public static String computeHash(String text, String salt, String algorithmName){

        return computeHash(text,salt,algorithmName,HASH_ITERATIONS);
    }

    public static String computeHash(String text, String salt, String algorithmName,int iterations){
        // 创建 SimpleHash 实例，并设置算法、源数据、盐和迭代次数
        return new SimpleHash(
                algorithmName,
                text,
                ByteSource.Util.bytes(salt),
                iterations
        ).toHex();
    }
}