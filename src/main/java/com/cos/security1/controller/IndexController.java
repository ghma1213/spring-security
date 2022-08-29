package com.cos.security1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    // localhost:8080/
    // localhost:8080
    @GetMapping({"", "/"})
    public String index() {
        // Mustach 대신 thymeleaf 사용
        // 기본 폴더 src/main/resources/
        //
        return "index";
    }
}
