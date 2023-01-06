package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @GetMapping
    public String findAll() {
        return "查询产品列表成功！";
    }
}
