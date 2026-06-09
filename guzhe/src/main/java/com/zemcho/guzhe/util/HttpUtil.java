package com.zemcho.guzhe.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HTTP请求工具类
 *
 * @title: HttpUtil
 * @Description:
 * @Date: 2023/7/21 9:31
 */
public class HttpUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 50000;

    /**
     * 读取超时时间
     */
    public static final int SO_TIMEOUT = 50000;

    /**
     * 创建HTTP通信的客户端
     *
     * @return
     */
    private static HttpClient getHttpClient() {
        try {
            // 创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

            // 创建管理授权证书工厂
            X509TrustManager trustManager = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {

                    return new X509Certificate[]{};
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

            // 创建SSLConnectionSocketFactory
            // sslContext SSL套接字协议; supportedProtocols 认证协议;
            // supportedCipherSuites 密码套件;hostnameVerifier主机认证
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    new String[]{"TLSv1.2"}, null, new DefaultHostnameVerifier());

            // 创建BasicHttpClientConnectionManager
            // socketFactoryRegistry 协议注册; connFactory 请求; schemePortResolver
            // 策略端口; dnsResolver 域名解析
            BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", sslConnectionSocketFactory).build(),
                    null, null, null);

            // 创建HttpClient
            HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();

            return httpClient;
        } catch (Exception e) {
            log.error("HttpUtil 创建HTTP通信的客户端失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送POST请求
     *
     * @param url    请求url
     * @param data   参数（xml格式或json格式）
     * @param header 请求头参数
     * @return
     */
    public static String sendPost(String url, String data, Map<String, String> header) {
        HttpPost httpPost = null;
        try {
            // 获取HttpClient
            HttpClient httpClient = getHttpClient();

            // 请求参数设置
            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();

            // 创建HttpPost
            httpPost = new HttpPost(url);

            httpPost.setConfig(requestConfig);

            StringEntity postEntity = new StringEntity(data, "UTF-8");
            if (isXml(data)) {
                httpPost.addHeader("Content-Type", "text/xml");
            } else {
                httpPost.addHeader("Content-Type", "application/json");
            }

            if (header != null && !header.isEmpty()) {
                for (String headerName : header.keySet()) {
                    httpPost.addHeader(headerName, header.get(headerName));
                }
            }

            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45" +
                    ".0");
            httpPost.setEntity(postEntity);

            // 创建HttpResponse
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            return EntityUtils.toString(httpEntity, "UTF-8");
        } catch (Exception e) {
            log.error("HttpUtil 发送POST请求失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (httpPost != null) {
                try {
                    httpPost.clone();
                } catch (Exception exception) {
                    log.error("HttpUtil 发送POST请求关闭HttpPost失败：" + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 发送POST请求
     *
     * @param url    请求url
     * @param data   参数（Map格式）
     * @param header 请求头参数
     * @return
     */
    public static String sendPost(String url, Map<String, Object> data, Map<String, String> header) {
        return sendPost(url, (String) JSON.toJSONString(data), header);
    }

    /**
     * 发送POST请求
     *
     * @param url  请求url
     * @param data 参数（Map格式）
     * @return
     */
    public static String sendPost(String url, Map<String, Object> data) {
        return sendPost(url, (String) JSON.toJSONString(data), null);
    }

    /**
     * 发送POST请求并返回响应header和数据
     *
     * @param url    请求url
     * @param data   参数（xml格式或json格式）
     * @param header 请求头参数
     * @return
     */
    public static Map<String, Object> sendPostRefundHeader(String url, String data, Map<String, String> header) {
        HttpPost httpPost = null;
        try {
            // 获取HttpClient
            HttpClient httpClient = getHttpClient();

            // 请求参数设置
            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();

            // 创建HttpPost
            httpPost = new HttpPost(url);

            httpPost.setConfig(requestConfig);

            StringEntity postEntity = new StringEntity(data, "UTF-8");
            if (isXml(data)) {
                httpPost.addHeader("Content-Type", "text/xml");
            } else {
                httpPost.addHeader("Content-Type", "application/json");
            }

            if (header != null && !header.isEmpty()) {
                for (String headerName : header.keySet()) {
                    httpPost.addHeader(headerName, header.get(headerName));
                }
            }

            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45" +
                    ".0");
            httpPost.setEntity(postEntity);

            // 创建HttpResponse
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            //响应header
            Header[] allHeader = httpResponse.getAllHeaders();
            Map<String, Object> headerMap = new HashMap<>();
            for (int i = 0; i < allHeader.length; i++) {
                Header hd = allHeader[i];
                headerMap.put(hd.getName(), hd.getValue());
            }

            //响应数据
            String dataStr = EntityUtils.toString(httpEntity, "UTF-8");

            Map<String, Object> result = new HashMap<>();
            result.put("header", headerMap);
            result.put("dataStr", dataStr);

            return result;
        } catch (Exception e) {
            log.error("HttpUtil 发送POST请求失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (httpPost != null) {
                try {
                    httpPost.clone();
                } catch (Exception exception) {
                    log.error("HttpUtil 发送POST请求关闭HttpPost失败：" + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 发送GET请求
     *
     * @param url  请求url
     * @param data 参数（Map格式）
     * @param header 请求头参数
     * @return
     */
    public static String sendGet(String url, Map<String, String> data, Map<String, String> header) {
        HttpGet httpGet = null;
        try {
            // 创建客户端
            HttpClient httpClient = getHttpClient();
            // 请求参数设置
            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(SO_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
            // 创建GET请求
            httpGet = new HttpGet();

            // 拼接url
            if (data != null) {
                String params = convertStringParam(data);
                url = url + "?" + params;
            }

            // 设置参数
            httpGet.setConfig(requestConfig);
            httpGet.setURI(new URI(url));

            if (header != null && !header.isEmpty()) {
                for (String headerName : header.keySet()) {
                    httpGet.addHeader(headerName, header.get(headerName));
                }
            }

            // 发送请求
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // 获取返回的数据
            HttpEntity entity = httpResponse.getEntity();
            String response = EntityUtils.toString(entity, "utf-8");

            return response;
        } catch (Exception e) {
            log.error("HttpUtil 发送GET请求失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (httpGet != null) {
                try {
                    httpGet.clone();
                } catch (Exception exception) {
                    log.error("HttpUtil 发送GET请求关闭HttpGet失败：" + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 判断是否是xml格式
     *
     * @param data
     * @return
     */
    private static boolean isXml(String data) {
        if (data.startsWith("<?xml")) {
            return true;
        }

        return false;
    }

    /**
     * 请求参数拼接
     *
     * @param params
     * @return
     */
    public static String convertStringParam(Map<String, String> params) {
        StringBuffer parameterBuffer = new StringBuffer();

        // 若参数为空不处理
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            String key = null;
            String value = null;

            while (iterator.hasNext()) {
                // 拼接参数
                key = (String) iterator.next();
                if (params.get(key) != null) {
                    value = (String) params.get(key);
                    parameterBuffer.append(key).append("=").append(value);
                }
                // 非末尾处添加 & 连接符
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }

        return parameterBuffer.toString();
    }
}
