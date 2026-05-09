package com.zemcho.guzhe.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zemcho.guzhe.util.decode.HashServiceUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @author Ryan
 * @title: KeyUtil
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/7/30 0030 10:26
 */
public class KeyUtil {
    public static String createKey() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        LocalDateTime now = LocalDateTime.now();

        int i = new Random().nextInt(1000) + 1000;

        String preToken = now.format(formatter) + i;

        return HashServiceUtil.computeHash(preToken, HashServiceUtil.SALT, "md5", 8).toUpperCase();
    }

    public static String createUserKey(){
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        /*LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        LocalDateTime now1 = LocalDateTime.now(ZoneId.systemDefault());
        System.out.println(now1);

        String password = HashServiceUtil.computeHash("ryan0721");
        System.out.println(password);*/

        //System.out.println(KeyUtil.createKey()+":"+KeyUtil.createKey());

        /*String s = "55506CC10E5847C5BA5338EB60C2D985\n" +
                "8FBEF11BCD4322A4B20D7B7FEFD983B6\n" +
                "34C7189CB48F18FB38FF6FE80B525A13\n" +
                "0F04C48A5397B870B85971D4C44306CE\n" +
                "2E83181D42CA08C704ADA8E7C9E3F3B8\n" +
                "22BEBA98BA85F4E6A1EAA607EB3E83A9\n" +
                "065E2E93763AC590803B4C6D6A6AEDB3\n" +
                "2412E8D06DF58F175954D8DE18F1E2A9\n" +
                "D7A91ADE6670854C396CED04B040F79C\n" +
                "E3BD6CBDCFEB409E0E30C4592B51D41E\n" +
                "67B6B6E9A9D1EFFA162DDB3710AB99EB\n" +
                "7DBDFC87628F1A26F8007E91FD5DB2A7\n" +
                "658A6BDE49AE5B2400C1F2484A48F878\n" +
                "B86122883D107191A0B129FE87BDED68\n" +
                "23DA571FDEF12AF2B00D1FFEF2EBCDD5\n" +
                "246F2A9B9315CE5F818E123083B51F21\n" +
                "A8955BFC8DEAFECD445AAFC84D6C663A\n" +
                "5AC9CA7CD2D1C6A5AC383362EB74A9FF\n" +
                "5F1A9AD4E20F60272BE87A1C830F4389\n" +
                "C01D090295857B336635A76E8A10299A\n" +
                "BCF35EF6D979601CD6476F39848188AC\n" +
                "A721DD659960F508119818E3CEB7409E\n" +
                "24BF1D31A1B113ACEAB9478F93A4735F\n" +
                "9E06D9362599AA4508D715DDFBDEA89F\n" +
                "EBD12C2F7E9C61EF7B558B127FF95783\n" +
                "96874451AA7635591C04100C1C2BF769\n" +
                "4CF7BDBCEEE3038FF128AD5463522A29\n" +
                "A56BDB28356D1F955BC5DD532D776A19\n" +
                "E10CE13210C86B2F28144109C8E3D9EA\n" +
                "A6D0D1CCE029F6FF90EE21FCA7D506CD\n" +
                "6A1909CB0CCF5C20600F1A48821C796A\n" +
                "DEF0D0E3169DCDB8ED8A8CF0B28F23F7\n" +
                "4103AAC85F2F04C351B480F77EB1489B\n" +
                "1E1B83FC55C2F3A5716E63FC29D4DAB4\n" +
                "027AF95D9BD31FD31002F16CE4A52015\n" +
                "533CC3C7EF842FE6B36087B77E03A36D\n" +
                "8F502989105003F6AB77AFF6D8B4B98A\n" +
                "070B4D99B373DF32455889260E21879D\n" +
                "CC50642A4454018AF699226CD890A7B9\n" +
                "F0E9DC7BDAD95EA2C0FE94CE58CA7BBE\n" +
                "B95C5D91C3E952638E6669671F9F2032\n" +
                "E798B8C4CF504C85AACB2C3D7FE9542B\n" +
                "FEE73F15A3DD7D9D9A430C0AF85CA8C6";

        String sa = s.replaceAll("\n",",");
        String sb = sa.replaceAll(",","\",\"");
        System.out.println(sb);*/

        String ip = "{\"aa\":\"aa\"}";
        JSONObject paramObj = JSON.parseObject(ip);
        String aa = paramObj.getString("a");
    }
}
