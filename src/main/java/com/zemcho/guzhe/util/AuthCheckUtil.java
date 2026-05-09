package com.zemcho.guzhe.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.*;

public class AuthCheckUtil {
    private static final String KEY = "";

    private static final String IV = "";

    private static final String APPID = "";

    private static final String URL = "http://auth.zemcho.com";


    public static String httpPost(Map<String, String> data){
        String body = "";
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(URL);

        //装填参数
        /*StringEntity s = new StringEntity(data.toString(), "UTF-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                ""));*/

        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        List<NameValuePair> params = new ArrayList<>();

        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            params.add(pair);
        }

        //执行请求操作，并拿到结果（同步阻塞）
        try {
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            System.out.println("请求地址："+URL);

            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                response.close();
                return body;
            }
            //获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                return EntityUtils.toString(entity);
            }
        }catch (Exception e) {
            return body;
        }
        return body;
    }

    /**
     * @description: 加密
     * @author Ryan
     * @date 2020/6/29 0029
     * @time 11:56
     */
    public static String DESEncrypt(String text) {
        try {
            // 进行DES3加密后的内容的字节
            DESedeKeySpec dks = new DESedeKeySpec(KEY.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
            Key skey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("desede"+"/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(IV.getBytes("utf-8"));
            cipher.init(Cipher.ENCRYPT_MODE, skey,ips);
            byte[] encryptedData = cipher.doFinal(text.getBytes("utf-8"));
            // 进行DES3加密后的内容进行BASE64编码
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e){
            return text;
        }
    }

    /**
     * @description: 解密
     * @author Ryan
     * @date 2020/6/29 0029
     * @time 11:56
     */
    public static String DESDecrypt(String text) {
        try {
            // 进行DES3加密后的内容进行BASE64解码
            byte[] base64DValue = Base64.getDecoder().decode(text);
            // 进行DES3解密后的内容的字节
            DESedeKeySpec dks = new DESedeKeySpec(KEY.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
            Key skey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("desede"+"/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skey,ips);
            byte[] encryptedData = cipher.doFinal(base64DValue);
            return new String(encryptedData,"utf-8");
        } catch (Exception e) {
            return text;
        }
    }
    
    public static String checkAuth(){
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        JSONObject data = new JSONObject();
        data.put("domain","127.0.0.1");
        data.put("db_ip","127.0.0.1");
        data.put("host_ip","127.0.0.1");
        data.put("mac","");
        data.put("timestamp",Integer.valueOf(timestamp));

        String ss = data.toJSONString();

        String sign = DESEncrypt(ss);

        Map<String,String> param = new HashMap<>();
        param.put("appid", APPID);
        param.put("sign",sign);
        param.put("timestamp",timestamp);

        String result = httpPost(param);
        
        return result;
    }
}