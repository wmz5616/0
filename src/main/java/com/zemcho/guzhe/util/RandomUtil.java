package com.zemcho.guzhe.util;

import org.apache.commons.lang.RandomStringUtils;

public class RandomUtil {

    public static String randomString(int count) {
        return RandomStringUtils.random(count, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }
}