package com.cloud.nsk.microservicegatewayzuul.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author nsk
 * 2018/11/14 8:05
 */
@Data
public class User {

    private Long id;

    private String username;

    private String name;

    private Integer age;

    private BigDecimal balance;

}
