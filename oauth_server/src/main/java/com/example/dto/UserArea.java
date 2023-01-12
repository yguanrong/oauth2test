package com.example.dto;

import lombok.Data;

/**
 * @Author:Mateo
 * @Description:
 * @Date: Created in 17:15 2020/9/16
 * @Modified By:
 */
@Data
public class UserArea {

    private Long id;

    private String areaId;

    private Long userId;

    /**
     * 区域等级 level
     */
    private Integer level;

    private String description;
}
