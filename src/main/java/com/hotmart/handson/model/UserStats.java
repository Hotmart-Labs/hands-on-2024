package com.hotmart.handson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {
    @Builder.Default
    private Long tweets = 0L;
    @Builder.Default
    private Long followers = 0L;
    @Builder.Default
    private Long following = 0L;
}
