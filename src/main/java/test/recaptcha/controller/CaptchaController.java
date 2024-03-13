package test.recaptcha.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import test.recaptcha.service.CaptchaService;
import test.recaptcha.dto.CaptchaResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/captcha")
@Slf4j
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping("/simpleCaptcha")
    public void simpleCaptcha(
            @RequestParam(required = false, defaultValue = "120") int captW,
            @RequestParam(required = false, defaultValue = "35") int captH,
            @RequestParam(required = false, defaultValue = "35") int captF,
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
        log.info("Call simpleCaptchaCheck");
        return captchaService.checkSimpleCaptcha(captchaAnswer, request);
    }

    @PostMapping("/check")
    public CaptchaResponse reCaptchaCheck(
            @RequestParam(required = false, defaultValue = "") String token,
            HttpServletRequest request
    ){
        log.info("Call reCaptchaCheck");
        return captchaService.checkReCaptcha(token, request.getRemoteHost());
    }

}
