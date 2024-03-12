package test.recaptcha.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import test.recaptcha.service.CaptchaService;
import test.recaptcha.vo.CaptchaResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/captcha")
@Slf4j
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping("/simpleCaptcha")
    public void simpleCaptcha(
            @RequestParam int captW,
            @RequestParam int captH,
            @RequestParam int captF,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        captchaService.getSimpleCaptcha(captW, captH, captF, request, response);
    }

    @PostMapping("/simpleCaptcha/check")
    public CaptchaResponse simpleCaptchaCheck(
            @RequestParam(required = false, defaultValue = "") String captchaAnswer,
            HttpServletRequest request
    ){
        String status = "";
        String message = "";

        if (captchaAnswer.equals("")){
            status = "FAIL";
            message = "captcha 번호를 입력하세요.";
            log.error("captcha 번호를 입력하지 않음");
        }else {
            // simpleCaptcha 검증
            if(captchaService.checkSimpleCaptcha(captchaAnswer, request)){
                status = "OK";
                message = "SimpleCaptcha 확인 완료";
            }else {
                status = "FAIL";
                message = "번호를 다시 확인해주세요.";
            }
        }

        return new CaptchaResponse(status, message);
    }

    @PostMapping("/check")
    public CaptchaResponse reCaptchaCheck(
            @RequestParam String token
    ){
        String status = "";
        String message = "";

        if(captchaService.checkReCaptcha(token)){
            status = "OK";
            message = "성공";
        }else {
            status = "FAIL";
            message = "실패";
        }

        return new CaptchaResponse(status, message);
    }

}
