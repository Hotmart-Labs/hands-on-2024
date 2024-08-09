package com.hotmart.handson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tweets")
public class Tweet {
    @Id
    private String uuid;
    private UserRef author;
    private String content;
    private String media;
    private Date createdAt;
    private TweetRef parent;
    @Builder.Default
    private TweetType type = TweetType.TWEET;
    @Builder.Default
    private TweetStats stats = TweetStats.builder().build();
    @Transient
    private boolean liked;
}
