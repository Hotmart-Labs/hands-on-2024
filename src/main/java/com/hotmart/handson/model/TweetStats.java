package com.hotmart.handson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetStats {
    @Builder.Default
    private Long replies = 0L;
    @Builder.Default
    private Long retweets = 0L;
    @Builder.Default
    private Long likes = 0L;
}
