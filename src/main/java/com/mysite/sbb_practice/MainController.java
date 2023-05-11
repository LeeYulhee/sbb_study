package com.mysite.sbb_practice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
// MVC에서 C에 해당하며, 주로 사용자의 요청을 처리한 후 지정된 뷰에 모델 객체를 넘겨주는 역할
public class MainController {

    @GetMapping("/sbb")
    // "/sbb"와 URL 연결
    @ResponseBody
    // URL 요청에 대한 응답으로 문자열을 return 하라는 뜻
    public String index() {
        return "안녕하세요, sbb에 오신 것을 환영합니다.";
    }

    @GetMapping("/")
    // "/"와 URL 연결
    public String root() {
        return "redirect:/question/list";
        // redirect로 해당 URL로 이동 -> "question/list"(질문 목록 페이지)로 이동
    }
}
