package com.zemcho.guzhe.config.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zemcho.guzhe.util.XssUtil;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class XSSHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest request;

    public XSSHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
    }

    @Override
    public String getHeader(String name) {
        return XssUtil.filter(super.getHeader(name));
    }

    @Override
    public String getParameter(String name) {
        return XssUtil.filter(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);

        if (values != null) {
            int i = 0;
            for (String value : values) {
                values[i++] = XssUtil.filter(value);
            }
        }

        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = new HashMap(super.getParameterMap());
        parameterMap.forEach((key, values) -> {
            int i = 0;
            for (String value : values) {
                values[i++] = XssUtil.filter(value);
            }
            parameterMap.put(key, values);
        });

        return parameterMap;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        String isMultipart = request.getContentType();
        if (isMultipart != null && isMultipart.indexOf("multipart") == -1) {

            String requestBody = requestBodyToString(request);

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes("utf-8"));
            return new ServletInputStream() {

                @Override
                public int read() throws IOException {

                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {

                    return false;
                }

                @Override
                public boolean isReady() {

                    return false;
                }

                @Override
                public void setReadListener(ReadListener arg0) {

                }
            };
        } else {
            ServletInputStream fileStream = request.getInputStream();
            return fileStream;
        }
    }

    private String requestBodyToString(HttpServletRequest request) throws IOException {
        Map<String, Object> map = requestBodyToMap(request);
        return JSON.toJSONString(map);
    }

    private Map<String, Object> requestBodyToMap(HttpServletRequest request) throws IOException {
        byte buffer[] = requestBodyToByte(request);
        Map<String, Object> map = new HashMap<>();
        if (buffer != null) {
            map = JSON.parseObject(buffer, Map.class);
        }
        filterMap(map);

        return map;
    }

    private byte[] requestBodyToByte(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    private void filterList(List list) {
        int i = 0;
        for (Object o : list) {
            if (o instanceof String) {
                o = XssUtil.filter((String) o);
            } else if (o instanceof JSONObject) {
                filterMap((Map) o);
            }

            list.set(i++, o);
        }
    }

    private void filterMap(Map map) {
        map.forEach((key, value) -> {

            if (value instanceof String) {
                value = XssUtil.filter((String) value);
            } else if (value instanceof JSONArray) {
                filterList((List) value);
            } else if (value instanceof JSONObject) {
                filterMap((Map) value);
            }

            map.put(key, value);
        });
    }
}
