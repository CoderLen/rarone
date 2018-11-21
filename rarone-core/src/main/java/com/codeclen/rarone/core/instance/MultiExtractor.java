package com.codeclen.rarone.core.instance;

import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.pipeline.Pipeline;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;

import java.util.HashMap;
import java.util.Map;

/**
 * 信息列表抽取
 * @author lin
 * @since 2018/11/16.
 */
public abstract class MultiExtractor extends Extractor {

    private String url;

    /**
     * 当前处理的页码，默认第1页
     */
    private Integer pageNo = 1;

    private Map<String, Object> params;

    private Pipeline pipeline;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * 提取数据
     * @param json
     * @param <T>
     * @return
     */
    public abstract <T> Page<T> extract(String json);

    public <T> Page<T> run(String url, Map<String, Object> params){
        Asserts.notEmpty(url, "url");
        if(StringUtils.isNotEmpty(url)){
            String response = null;
            if(responseType == ResponseType.JSON){
                response = request(url, params);
            }else if(responseType == ResponseType.HTML && params != null && params.size() > 0){
                response = requestHtml(url, params);
            }else {
                response = requestByHttpUnit(url);
            }
            if(response != null){
                return extract(response);
            }else {

            }
        }
        return null;
    }


    public <T> Page<T> run(String url){
        if(StringUtils.isNotEmpty(url)){
            String response = request(url, null);
            if(response != null){
                return extract(response);
            }else {

            }
        }
        return null;
    }

    public MultiExtractor withUrl(String url){
        this.url = url;
        return this;
    }

    public MultiExtractor withParams(Map<String, Object> params){
        this.params = params;
        return this;
    }

    /**
     * 设置初始的页码
     * @param pageNoParamName
     * @param pageNo
     * @return
     */
    public MultiExtractor withPageNo(String pageNoParamName, Integer pageNo){
        this.pageNo = pageNo;
        if(this.params != null){
            this.params.put(pageNoParamName, pageNo);
        }
        return this;
    }

    public MultiExtractor withPipeline(Pipeline pipeline){
        this.pipeline = pipeline;
        return this;
    }

    public MultiExtractor addParam(String key, Object value){
        if(params == null){
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public void action(){
        Asserts.notEmpty(this.url, "url");
        Asserts.notNull(this.pipeline, "pipeline");
        String response = request(url, params);
        if(response != null){
            Page page = extract(response);
            pipeline.process(page.getData());
        }
    }

    public void actionGet(){
        Asserts.notEmpty(this.url, "url");
        Asserts.notNull(this.pipeline, "pipeline");
        String response = request(url, this.params);
        if(response != null){
            Page page = extract(response);
            page.setPageNo(this.pageNo);
            pipeline.process(page.getData());
            while (page != null && this.pageNo * page.getPageSize() < page.getTotalCount()){
                this.pageNo = this.pageNo + 1;
                this.params.put(page.getPageNoParamName(), this.pageNo);
                actionGet();
            }
        }
    }

}
