package com.cos.security1.controller;

import com.cos.security1.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // View를 리턴!
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    @ResponseBody
    public String testLogin(Authentication authentication
            , @AuthenticationPrincipal PrincipalDetails userDetails) { // DI (의존성주입)
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());

        System.out.println("userDetails = " + userDetails.getUsername());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    @ResponseBody
    public String testOAuthLogin(Authentication authentication
            , @AuthenticationPrincipal OAuth2User oAuth) { // DI (의존성주입)
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());
        System.out.println("oAuth.getAttributes() = " + oAuth.getAttributes());
        return "세션 정보 확인하기";
    }

    // 세션 안에 스프링 시큐리티는 자기만의 시큐리티 세션이 있다.
    // 시큐리티 세션 안에 들어갈 수 있는 객체는 Authentication 타입만 가능
    // Authentication 타입에는 2가지만 가능 (UserDetails(일반 로그인), OAuth2User(OAuth 로그인)) => PricipalDetails로 둘 다 받아서 사용

    // localhost:8080/
    // localhost:8080
    @GetMapping({"", "/"})
    public String index() {
        // Mustach 대신 thymeleaf 사용
        // 기본 폴더 src/main/resources/
        return "index";
    }

    @GetMapping("/user")
    @ResponseBody
    public String user() {
        return "user";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    // 시큐리티 설정 안하면 스프링 시큐리티가 해당 주소를 낚아챈다.
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println("user = " + user);
        user.setRole("ROLE_USER");

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        user.setPassword(encPassword);

        userRepository.save(user); // 회원가입은 잘되지만, 비밀번호 암호화 필요

        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN") // 한 개의 권한만 확인 할 경우
    @GetMapping("/info")
    @ResponseBody
    public String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 여러 개의 권한을 확인 할 경우 // 메소드 시작 전
    // @PostAuthorize(): 메소드 시작 후
    @GetMapping("/data")
    @ResponseBody
    public String data() {
        return "데이터정보";
    }

}
