package com.example.controller;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@Controller
public class HomeController {

//    @RequestMapping("login")
//    public String login() {
//        System.out.println("执行登录逻辑");
//        return "redirect:main.html";
//    }

    @RequestMapping(value = "/toMain")
    public String toMain() {
        System.out.println("执行登录成功跳转");
        return "redirect:main.html";
    }

    @RequestMapping(value = "/toFailure")
    @ResponseBody
    public Map<String,Object> toFailure() {
        Map<String,Object> map = new HashMap<>();
        map.put("error","权限不足");
        map.put("code","403");
        return map;
    }
}
