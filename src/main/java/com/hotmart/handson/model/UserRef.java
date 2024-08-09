package com.hotmart.handson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRef {
    private String uuid;
    private String name;
    private String username;
    private String picture;
    private boolean verified;
}
