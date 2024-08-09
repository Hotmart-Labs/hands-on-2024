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
public class RelationshipVO {
    @Id
    private String uuid;
    private String user;
    private String follower;
    private Date createdAt;
}
