package com.codeclen.rarone.core.instance.bthhotel.extract;

import com.codeclen.rarone.core.Page;
import com.codeclen.rarone.core.instance.AbstractHotelExtractor;
import com.codeclen.rarone.core.instance.Comment;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lin
 * @since 2018/11/16.
 */
public class BthCommentExtractor extends AbstractHotelExtractor {

    private static Pattern pattern = Pattern.compile("[0-9.]+");

    public BthCommentExtractor(){
        this.responseType = ResponseType.HTML;
    }

    @Override
    public Page<Comment> extract(String html) {
        Page<Comment> page = new Page<>();
        List<Comment> comments = new LinkedList<>();
        page.setData(comments);
        Document document = Jsoup.parse(html);
        //总条数
        String totalVal = document.select("#TotalRows").val();
        Integer total = StringUtils.isNotEmpty(totalVal) ? Integer.parseInt(totalVal) : 0;
        page.setTotalCount(total);
        //当前页码, 这个页面的pageNo不正常
//        String pageNoVal = document.select("#pageNo").val();
//        Integer pageNo = StringUtils.isNotEmpty(pageNoVal) ? Integer.parseInt(pageNoVal) : 0;
//        page.setPageNo(pageNo);
//        page.setPageNoParamName("pageNo");

        Elements elements = document.select("dl.fix");
        elements.forEach(element -> {
            String content = element.select("dd > p.comment").text();
            String reply = element.select("dd > p.reply").text();
            String commentDate = element.select("dd > p.score_date > span.date").text();
            String score = element.select("dd > p.score_date > span.score").text();
            Comment comment = new Comment();
            comment.setComment(content);
            comment.setCreateTime(commentDate);
            Matcher matcher = pattern.matcher(score);
            comment.setEntireScore(matcher.find() ? Double.parseDouble(matcher.group()) : 0.0);
            comment.setHotelReply(reply);
            comment.setNickname(element.select("dt > p.number").text());
            comments.add(comment);
        });
        return page;
    }
}
