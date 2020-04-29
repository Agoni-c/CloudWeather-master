package com.example.cyk.cloudweather.bean;

import com.example.cyk.cloudweather.gson.Lifestyle;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * {
 *     "reason": "成功的返回",
 *     "result": {
 *         "stat": "1",
 *         "data": [
 *       {
 *         "uniquekey": "6c4caa0c3ba6e05e2a272892af43c00e",
 *         "title": "杨幂的发际线再也回不去了么？网友吐槽像半秃",
 *         "date": "2017-01-05 11:03",
 *         "category": "yule",
 *         "author_name": "腾讯娱乐",
 *         "url": "http://mini.eastday.com/mobile/170105110355287.html?qid=juheshuju",
 *         "thumbnail_pic_s": "http://03.imgmini.eastday.com/mobile/20170105/20170105110355_
 * 806f4ed3fe71d04fa452783d6736a02b_1_mwpm_03200403.jpeg",
 *         "thumbnail_pic_s02": "http://03.imgmini.eastday.com/mobile/20170105/20170105110355_
 * 806f4ed3fe71d04fa452783d6736a02b_2_mwpm_03200403.jpeg",
 *         "thumbnail_pic_s03": "http://03.imgmini.eastday.com/mobile/20170105/20170105110355_
 * 806f4ed3fe71d04fa452783d6736a02b_3_mwpm_03200403.jpeg"
 *     },
 * ...]}}
 */
public class HeadlineNewsBean {
    public String stat;

    @SerializedName("data")
    public List<NewsBean> newsBeanList;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public List<NewsBean> getNewsBeanList() {
        return newsBeanList;
    }

    public void setNewsBeanList(List<NewsBean> newsBeanList) {
        this.newsBeanList = newsBeanList;
    }
}
