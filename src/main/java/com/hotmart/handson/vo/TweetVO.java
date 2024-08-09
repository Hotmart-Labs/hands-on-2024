package com.hotmart.handson.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TweetVO {
    private String uuid;
    private UserVO author;
    private String content;
    private String media;
    private Date createdAt;
    private String type;
    private TweetRefVO parent;
    private TweetStatsVO stats;
    private boolean liked;
}
