package com.tang.permission.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
@Setter
public class SearchLogDto {

    private Integer type; //logType的值

    private String beforeSeq;

    private String afterSeq;

    private String operator;

    private Date fromTime; //yyyy-MM-dd HH:mm:ss

    private Date toTime;
}
