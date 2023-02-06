package com.intellif.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Liangzhifeng
 */
@Data
public class Operation implements Serializable {

    private static final long serialVersionUID = 6519326685475152368L;

    private Long id;

    private String name;

    private String url;

    private String method;

    private Date createTime;

    private Date updateTime;

    private Integer deletedFlg;

    private Integer rightType;
}