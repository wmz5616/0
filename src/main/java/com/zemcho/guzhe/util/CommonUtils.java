package com.zemcho.guzhe.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * @author Ryan
 * @title: CommonUtils
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/9/28 0028 17:23
 */
public class CommonUtils {
    /**
     * 默认IP地址
     */
    public final static String ERROR_IP = "127.0.0.1";

    public static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    public static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");

    public static String longToIpV4(long longIp) {
        int octet3 = (int) ((longIp >> 24) % 256);
        int octet2 = (int) ((longIp >> 16) % 256);
        int octet1 = (int) ((longIp >> 8) % 256);
        int octet0 = (int) ((longIp) % 256);
        return octet3 + "." + octet2 + "." + octet1 + "." + octet0;
    }

    public static long ipV4ToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
                + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
    }

    public static boolean isIPv4Private(String ip) {
        long longIp = ipV4ToLong(ip);
        return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
                || (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
                || longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
    }

    public static boolean isIPv4Valid(String ip) {
        return pattern.matcher(ip).matches();
    }

    /*public static String getUserIP(HttpServletRequest request) {
        String ip;
        boolean found = false;
        if ((ip = request.getHeader("x-forwarded-for")) != null) {
            StrTokenizer tokenizer = new StrTokenizer(ip, ",");
            while (tokenizer.hasNext()) {
                ip = tokenizer.nextToken().trim();
                if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }*/

    public static String getUserIP(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        ip = "0:0:0:0:0:0:0:1".equals(ip) ? ERROR_IP : ip;
        return ip;
    }

    public static String readAll(Reader rd) {
        StringBuilder sb = new StringBuilder();
        int cp;
        while (true) {
            try {
                if (!((cp = rd.read()) != -1)) {
                    break;
                } else {
                    sb.append((char) cp);
                }
            } catch (IOException e) {
                return "";
            }
        }
        return sb.toString();
    }

    /**
     * 创建链接
     *
     * @param ip
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static String getIpAddress(String ip) {
        String url = "http://freeapi.ipip.net/" + ip;
        InputStream is = null;
        try {
            is = new URL(url).openStream();
        } catch (IOException e) {
            return "";
        }
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = JSONArray.parseArray(jsonText);
            return json.getString(0) + json.getString(1) + json.getString(2) + json.getString(4);
        } catch (Exception e) {
            return "";
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                return "";
            }
        }
    }

    public static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");

        int ipAddr = (Integer.parseInt(ips[0]) << 24)

                | (Integer.parseInt(ips[1]) << 16)

                | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);

        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));

        int mask = 0xFFFFFFFF << (32 - type);

        String cidrIp = cidr.replaceAll("/.*", "");

        String[] cidrIps = cidrIp.split("\\.");

        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)

                | (Integer.parseInt(cidrIps[1]) << 16)

                | (Integer.parseInt(cidrIps[2]) << 8)

                | Integer.parseInt(cidrIps[3]);

        return (ipAddr & mask) == (cidrIpAddr & mask);

    }

    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            String regex = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}?" +
                    "(/*\\d)";
            if (text.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 数值格式化
     *
     * @param number      数值
     * @param keepDecimal 是否保留小数
     * @param plusSign    是否显示+符号
     * @return
     */
    public static String numberFormat(long number, boolean keepDecimal, boolean plusSign) {
        String suffix = plusSign ? "+" : "";

        if (number >= 100000000) {
            double yi = number / 100000000.0;
            return numberToString(yi, keepDecimal) + "亿" + suffix;
        } else if (number >= 10000) {
            double wan = number / 10000.0;
            return numberToString(wan, keepDecimal) + "万" + suffix;
        } else if (number >= 1000) {
            double qian = number / 1000.0;
            return numberToString(qian, keepDecimal) + "千" + suffix;
        } else {
            return String.valueOf(number);
        }
    }

    /**
     * 数值转字符串
     *
     * @param num         数值
     * @param keepDecimal 是否保留小数
     * @return
     */
    private static String numberToString(double num, boolean keepDecimal) {
        if (keepDecimal && num < 10) {
            return String.format("%.1f", num);
        } else {
            return String.valueOf((int) num);
        }
    }
}
