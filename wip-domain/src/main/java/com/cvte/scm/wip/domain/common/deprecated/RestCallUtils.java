package com.cvte.scm.wip.domain.common.deprecated;

import cn.hutool.core.collection.CollectionUtil;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.common.token.service.AccessTokenService;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.net.URLEncoder.encode;
import static java.util.Objects.isNull;
import static jodd.http.HttpBase.HEADER_CONTENT_TYPE;
import static jodd.util.MimeTypes.MIME_APPLICATION_JSON;
import static jodd.util.StringPool.UTF_8;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * 这是一个 RESTful 接口调用的工具类。
 *
 * @author : jf
 * Date    : 2019.02.20
 * Time    : 20:01
 * Email   ：jiangfeng7128@cvte.com
 */
@Component
@SuppressWarnings("unused")
public class RestCallUtils {

    private static final String X_IAC_TOKEN = "x-iac-token";

    private static final String TOKEN = "token";

    private static RestCallUtils restCallUtils;

    private final AccessTokenService accessTokenService;

    public RestCallUtils(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @PostConstruct
    public void init() {
        restCallUtils = this;
    }

    private static AccessTokenService getAccessTokenService() {
        return restCallUtils.accessTokenService;
    }

    public static String callRest(RequestMethod method, String destination, Map<String, Object> formMap) {
        return getResponseBody(initRequest(method, destination, formMap).send());
    }

    public static String callRest(RequestMethod method, String destination, String body) {
        return getResponseBody(createHttpRequest(method, destination, null, body, null, null).send());
    }

    public static String callRest(RequestMethod method, String destination, String xIacToken, String body) {
        return getResponseBody(createHttpRequest(method, destination, xIacToken, body, null, null).send());
    }

    public static String callRest(RequestMethod method, String destination, String xIacToken, Map<String, Object> formMap) {
        return getResponseBody(createHttpRequest(method, destination, xIacToken, null, null, formMap).send());
    }

    public static String callRest(RequestMethod method, String destination, String xIacToken, Map<String, String> headerMap,
                                  Map<String, Object> formMap) {
        return getResponseBody(createHttpRequest(method, destination, xIacToken, null, headerMap, formMap).send());
    }

    private static String callRest(RequestMethod method, String destination, String xIacToken, String body,
                                   Map<String, String> headerMap, Map<String, Object> formMap) {
        return getResponseBody(createHttpRequest(method, destination, xIacToken, body, headerMap, formMap).send());
    }

    /**
     * 根据请求方法 {@param method} 、请求地址 {@param destination} 以及表单信息 {@param formMap} 初始化一个请求对象 {@link HttpRequest}。
     */
    @SneakyThrows
    private static HttpRequest initRequest(RequestMethod method, String destination, Map<String, Object> formMap) {
        if (isNull(method)) {
            throw new IllegalArgumentException("请求方法为空。");
        } else if (StringUtils.isEmpty(destination)) {
            throw new IllegalArgumentException("请求地址为空。");
        }
        HttpRequest request = new HttpRequest().method(method.name()).header(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON);
        if (method == RequestMethod.GET) {
            request.set(destination.concat(toQueryString(formMap)));
        } else {
            request.set(destination).form(Optional.ofNullable(formMap).orElse(Collections.emptyMap()));
        }
        String accessToken = getAccessTokenService().getAccessToken();
        return request.charset(UTF_8).header(TOKEN, accessToken).header(X_IAC_TOKEN, accessToken);
    }

    /**
     * 根据已知信息获取请求对象 {@link HttpRequest} 。
     *
     * @param method      请求方法枚举，具体参见 {@link RequestMethod}。
     * @param destination 请求地址
     * @param xIacToken   IAC的Token值
     * @param body        请求Body值，默认格式为 UTF-8 的 JSON 字符串。
     * @param headerMap   请求头信息
     * @param formMap     表单信息
     */
    private static HttpRequest createHttpRequest(RequestMethod method, String destination, String xIacToken, String body,
                                                 Map<String, String> headerMap, Map<String, Object> formMap) {
        HttpRequest request = initRequest(method, destination, formMap);
        if (CollectionUtil.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.header(entry.getKey(), entry.getValue(), true);
            }
        }
        if (StringUtils.isNotEmpty(xIacToken)) {
            request.header(X_IAC_TOKEN, xIacToken, true);
        }
        if (StringUtils.isNotEmpty(body)) {
            request.bodyText(body, MIME_APPLICATION_JSON, UTF_8);
        }
        return request;
    }

    /**
     * 获取响应体内容(字符串)，如果响应 {@param response} 的状态为 "非OK"，则抛出异常。
     */
    private static String getResponseBody(HttpResponse response) {
        if (response.charset(UTF_8).statusCode() == SC_OK) {
            return response.bodyText();
        }
        throw new RuntimeException(response.bodyText());
    }

    /**
     * 将表单信息 {@param formMap} 转换为 URL 的 query 字符串。
     */
    @SneakyThrows
    private static String toQueryString(Map<String, Object> formMap) {
        if (CollectionUtil.isEmpty(formMap)) {
            return "";
        }
        StringBuilder query = new StringBuilder("?");
        for (Map.Entry<String, Object> param : formMap.entrySet()) {
            query.append(param.getKey()).append("=")
                    .append(encode(param.getValue().toString(), "UTF-8")).append("&");
        }
        query.setLength(query.length() - 1);
        return query.toString();
    }

    /**
     * 请求方法枚举类
     */
    public enum RequestMethod {
        GET, POST, DELETE, PUT, OPTIONS, TRACE, HEAD, PATCH
    }
}