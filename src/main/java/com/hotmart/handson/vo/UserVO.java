package com.hotmart.handson.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private String uuid;
    private String name;
    private String username;
    private String bio;
    private String location;
    private String link;
    private String picture;
    private String banner;
    private Date joinedAt;
    private UserStatsVO stats;
    private boolean following;
}
