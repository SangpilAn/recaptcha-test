package test.recaptcha.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@Slf4j
public class ViewController {

    @GetMapping("/login")
    public String loginPage(){
        return "redirect:/html/client.html";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam(required = false, defaultValue = "") String id,
            @RequestParam(required = false, defaultValue = "") String pw
    ) {
        //id/pw 검증
        if (id.equals("test") && pw.equals("1")){
            return "login";
        }else {
            log.error("아이디 패스워드 확인 필요 : id={}, pw={}", id, pw);
            return "redirect:/html/error.html";
        }
    }

}
