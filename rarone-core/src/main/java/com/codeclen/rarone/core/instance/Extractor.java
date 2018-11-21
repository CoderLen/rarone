package com.codeclen.rarone.core.instance;

import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author lin
 * @since 2018/11/14.
 */
@Slf4j
public class Extractor {


    protected ResponseType responseType = ResponseType.JSON;

    public enum ResponseType{
        JSON,
        HTML,
        ASYNHTML;
    }

    private static WebClient webClient;

    static {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000);
    }

    /**
     * 请求接口获取json信息
     * @param url
     * @param params
     * @return
     */
    protected String request(@NonNull String url, Map<String, Object> params){
        if(this.responseType == ResponseType.JSON){
            return requestJson(url, params);
        }else if(this.responseType == ResponseType.HTML && params != null){
            return requestHtml(url, params);
        }else if(this.responseType == ResponseType.ASYNHTML){
            return requestByHttpUnit(url);
        }else {
            throw new IllegalArgumentException("Not support!");
        }
    }


    /**
     * 请求接口获取json信息
     * @param url
     * @param params
     * @return
     */
    protected String requestJson(@NonNull String url, Map<String, Object> params){
        HttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        request.addHeader("Accept", "application/json;charset=UTF-8");
        request.addHeader("Content-Type", "application/json;charset=UTF-8");
        if(params != null && !params.isEmpty()){
            StringEntity entity = new StringEntity(JSON.toJSONString(params), "UTF-8");
            request.setEntity(entity);
        }
        try {
            HttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            String resStr = EntityUtils.toString(responseEntity, "UTF-8");
            return resStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            request.abort();
        }
        return null;
    }

    /**
     * 请求接口获取网页数据
     * @param url
     * @param params
     * @return
     */
    protected String requestHtml(@NonNull String url, Map<String, Object> params) {
        HttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        if(params != null && !params.isEmpty()){
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            for(Map.Entry<String,Object> entry : params.entrySet()){
                postParameters.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
            try {
                request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        try {
            HttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            String resStr = EntityUtils.toString(responseEntity, "UTF-8");
            return resStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            request.abort();
        }
        return null;
    }

    /**
     * 利用HttpUnit加载页面，可实现等待js异步渲染页面
     * @param url
     * @return
     * @throws IOException
     */
    public String requestByHttpUnit(String url) {
        HtmlPage htmlPage = null;
        try {
            htmlPage = webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(2000);
            String htmlString = htmlPage.asXml();
            return htmlString;
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            return "";
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        } finally {
            webClient.close();
        }
    }



}
