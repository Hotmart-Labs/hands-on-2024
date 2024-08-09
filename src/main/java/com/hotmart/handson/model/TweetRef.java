package com.hotmart.handson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetRef {
    private String uuid;
    private UserRef author;
    private String content;
    private String media;
    private Date createdAt;
}
