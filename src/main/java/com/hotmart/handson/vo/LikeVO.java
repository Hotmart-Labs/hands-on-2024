package com.hotmart.handson.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeVO {
    @Id
    private String uuid;
    private String tweet;
    private String user;
    private Date createdAt;
}
