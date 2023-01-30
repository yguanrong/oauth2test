package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Slf4j
public class RestTemplateUtil {

    /**
     * http请求
     */
    public static RestTemplate restTemplate;
    /**
     * https请求
     */
    public static RestTemplate restTemplateHttps;

    static {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(5 * 1000); // 超期时间设置为5s
        clientHttpRequestFactory.setReadTimeout(5 * 1000); // 超期时间设置为5s
        restTemplate = new RestTemplate(clientHttpRequestFactory);

        HttpsClientRequest httpsClientRequest = new HttpsClientRequest();
        httpsClientRequest.setConnectTimeout(5 * 1000); // 超期时间设置为5s
        httpsClientRequest.setReadTimeout(5 * 1000); // 超期时间设置为5s
        restTemplateHttps = new RestTemplate(httpsClientRequest);

    }

    /**
     * 通过json发送post请求
     */
    public static JSONObject doPost(String url, Map<String, Object> params) {

        // 1.创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        //2.将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(params, headers);

        //3.请求
        log.info("RestTemplateUtil doPost request url:{}, params:{}", url, JSONObject.toJSONString(params));
        RestTemplate client = url.startsWith("https") ? restTemplateHttps : restTemplate;
        JSONObject response = client.postForObject(url, requestEntity, JSONObject.class);
        log.info("RestTemplateUtil doPost response:{}", JSONObject.toJSONString(response));
        return response;
    }

    public static JSONObject doPost(String url, Map<String, Object> params, HttpHeaders headers) {

        //1.将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(params, headers);

        //2.请求
        log.info("RestTemplateUtil doPost request url:{}, params:{}", url, JSONObject.toJSONString(params));
        RestTemplate client = url.startsWith("https") ? restTemplateHttps : restTemplate;
        JSONObject response = client.postForObject(url, requestEntity, JSONObject.class);
        log.info("RestTemplateUtil doPost response:{}", JSONObject.toJSONString(response));
        return response;
    }


    /**
     * Object 参数实体类
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static JSONObject postWithObject(String url, Object params, HttpHeaders headers) {

        RestTemplate client = url.startsWith("https") ? restTemplateHttps : restTemplate;
        //1.将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(params, headers);
        //2.请求
        log.info("RestTemplateUtil postWithJson request url:{}, params:{}", url, JSONObject.toJSONString(params));
        JSONObject response = client.postForObject(url, requestEntity, JSONObject.class);
        log.info("RestTemplateUtil postWithJson response:{}", JSONObject.toJSONString(response));

        // 处理返回值
        return response;
    }

    public static String getWithMap(String url, Map<String,Object> params) {

        RestTemplate client = url.startsWith("https") ? restTemplateHttps : restTemplate;

        //2.请求
        log.info("RestTemplateUtil postWithJson request url:{}, params:{}", url, JSONObject.toJSONString(params));
        JSONObject response = client.getForObject(url,JSONObject.class,params);
        log.info("RestTemplateUtil postWithJson response:{}", JSONObject.toJSONString(response));

        // 处理返回值
        return JSONObject.toJSONString(response);
    }



    public static HttpHeaders getHeader(Map<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type",MediaType.APPLICATION_JSON_VALUE);
        params.forEach(headers::set);
        return headers;
    }

}
