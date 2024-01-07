package com.banmao.yuquedownloader.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Builder
public class FakeBrowser implements Serializable {

    private String host;

    private HashMap<String, Object> cookies;

    @Builder.Default
    private Map<String, String> headers = new HashMap<>();

    @Builder.Default
    private Integer timeout = 15000;

    @Builder.Default
    private Boolean setProxy = false;

    private static String proxyIP;

    private static Integer proxyPort;

    static {
        proxyIP = "127.0.0.1";
        proxyPort = 10809;
    }

    private String buildUrl(String url) {
        if (host == null) {
            return url;
        }
        String completeUrl = URLUtil.completeUrl(URLUtil.normalize(host).replace("http://", "https://"), url);
        if (completeUrl.lastIndexOf("/") != completeUrl.length() -1) {
            completeUrl += "/";
        }
        return completeUrl;
    }

    public void addHeader(String header, String content) {
        headers.put(header, content);
    }

    public byte[] sendImageGet(String url, Map<String, Object> params) {
        try (HttpResponse response = sendRequest(url, params)) {

            return response.bodyBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse sendRequest(String url, Map<String, Object> params) {
        url = buildUrl(url);
        StringBuilder urlBuilder = new StringBuilder(URLUtil.normalize(url));
        if (CollectionUtil.isNotEmpty(params)) {
            if (url.lastIndexOf("/") == url.length() - 1) {
                urlBuilder = new StringBuilder(url.substring(0, url.lastIndexOf("/")));
            }
            urlBuilder.append("?");
            urlBuilder.append(UrlQuery.of(params).build(StandardCharsets.UTF_8, false));
        }

        log.debug("request:{}", urlBuilder);

        HttpRequest request = HttpRequest.get(urlBuilder.toString())
                .cookie(getHttpCookies())
                .timeout(timeout)
                .addHeaders(headers);

        if (setProxy) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyIP, proxyPort));
            request.setProxy(proxy);
        }

        return request.execute();
    }

    public String sendGet(String url, Map<String, Object> params) {
        try (HttpResponse response = sendRequest(url, params)) {

            //log.debug("response:{}", response);
            return response.body();
        } catch (HttpException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<HttpCookie> getHttpCookies() {
        ArrayList<HttpCookie> httpCookies = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(cookies)) {
            for (String key : cookies.keySet()) {
                HttpCookie httpCookie = new HttpCookie(key, String.valueOf(cookies.get(key)));
                httpCookies.add(httpCookie);
            }
        }
        return httpCookies;
    }

}
