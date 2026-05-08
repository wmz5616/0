package com.zemcho.guzhe.util.decode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.Key;
import java.util.Base64;

public class AesEncode {
    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "zemcho@master161104";

    private static String KEY2 = "rPgEVmxhvxaWSdhOTgcnqJrp";

    private static String IV = "zemcho@master161104";

    private static String IV2 = "62389533";


    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data) {
        //前端未改，先注释
        /*try {
            byte[] encrypted1 = new Base64().decode(data);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString.replaceAll("[\u0000]", "");
        } catch (Exception e) {
            System.out.println("aes加密函数出现错误。");
        }*/
        return data;
    }

    /**
     * 加密
     *
     * @param text
     * @return
     */
    public static String DESEncrypt(String text) {
        try {
            // 进行DES3加密后的内容的字节
            DESedeKeySpec dks = new DESedeKeySpec(KEY2.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
            Key skey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(IV2.getBytes("utf-8"));
            cipher.init(Cipher.ENCRYPT_MODE, skey, ips);
            byte[] encryptedData = cipher.doFinal(text.getBytes("utf-8"));
            // 进行DES3加密后的内容进行BASE64编码
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     *
     * @param text
     * @return
     */
    public static String DESDecrypt(String text) {
        try {
            // 进行DES3加密后的内容进行BASE64解码
            byte[] base64DValue = Base64.getDecoder().decode(text);
            // 进行DES3解密后的内容的字节
            DESedeKeySpec dks = new DESedeKeySpec(KEY2.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
            Key skey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(IV2.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skey, ips);
            byte[] encryptedData = cipher.doFinal(base64DValue);
            return new String(encryptedData, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}