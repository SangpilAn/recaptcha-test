package test.recaptcha.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import test.recaptcha.dto.CaptchaResponse;
import test.recaptcha.dto.SimpleCaptchaInfo;
import test.recaptcha.service.CaptchaService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/captcha")
@Slf4j
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping("/simpleCaptcha")
    public void getSimpleCaptcha(
            @ModelAttribute SimpleCaptchaInfo simpleCaptchaInfo,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        captchaService.getSimpleCaptcha(simpleCaptchaInfo, request, response);
    }

    @PostMapping("/simpleCaptcha/check")
    public CaptchaResponse checkSimpleCaptcha(
            @RequestParam(required = false, defaultValue = "") String captchaAnswer,
            HttpServletRequest request
    ){
        log.info("Call checkSimpleCaptcha");
        return captchaService.checkSimpleCaptcha(captchaAnswer, request);
    }

    @PostMapping("/check")
    public CaptchaResponse checkReCaptcha(
            @RequestParam(required = false, defaultValue = "") String token,
            HttpServletRequest request
    ){
        log.info("Call checkReCaptcha");
        return captchaService.checkReCaptcha(token, request.getRemoteHost());
    }

}
