package com.hotmart.handson.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorVO {
    private Date timestamp;
    private String requestId;
    private String path;
    private Integer status;
    private String error;
    private String message;
    private String trace;
}
