package com.tang.permission.bean;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mail {

    private String subject;

    private String message;

    private Set<String> receivers;

}
