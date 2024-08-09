package com.hotmart.handson.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetCreateVO {

    @NotEmpty
    private String username;

    @NotEmpty
    private String content;

    private String tweetReplied;
}
