package com.example.cyk.cloudweather.bean;

import com.google.gson.annotations.SerializedName;

public class NewsBean {
    @SerializedName("uniquekey")    //唯一key
    public String uniquekey;

    @SerializedName("title")    //标题
    public String title;

    @SerializedName("date")    //日期时间
    public String date;

    @SerializedName("category")    //类别
    public String category;

    @SerializedName("author_name")    //作者
    public String author_name;

    @SerializedName("url")    //链接
    public String url;

    @SerializedName("thumbnail_pic_s")        //图片
    public String thumbnail_pic_s;

    @SerializedName("thumbnail_pic_s02")
    public String thumbnail_pic_s02;

    @SerializedName("thumbnail_pic_s03")
    public String thumbnail_pic_s03;

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail_pic_s() {
        return thumbnail_pic_s;
    }

    public void setThumbnail_pic_s(String thumbnail_pic_s) {
        this.thumbnail_pic_s = thumbnail_pic_s;
    }

    public String getThumbnail_pic_s02() {
        return thumbnail_pic_s02;
    }

    public void setThumbnail_pic_s02(String thumbnail_pic_s02) {
        this.thumbnail_pic_s02 = thumbnail_pic_s02;
    }

    public String getThumbnail_pic_s03() {
        return thumbnail_pic_s03;
    }

    public void setThumbnail_pic_s03(String thumbnail_pic_s03) {
        this.thumbnail_pic_s03 = thumbnail_pic_s03;
    }
}
