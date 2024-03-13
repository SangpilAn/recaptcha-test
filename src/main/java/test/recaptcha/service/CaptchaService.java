package test.recaptcha.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.DropShadowGimpyRenderer;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import test.recaptcha.dto.CaptchaResponse;
import test.recaptcha.dto.ReCaptchaJsonObject;
import test.recaptcha.util.CaptchaServletUtil;
import test.recaptcha.util.ReCaptchaUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CaptchaService {

    /**
     * SimpleCaptcha 이미지 생성
     * @param width 이미지 넓이
     * @param height 이미지 높이
     * @param fontsize 이미지 폰트 사이즈
     * @param request 세션에 captcha 를 담기 위해 사용
     * @param response 이미지를 저장하기 위해 사용
     */
    public void getSimpleCaptcha(int width, int height, int fontsize, HttpServletRequest request, HttpServletResponse response) {
        try {
            //폰트설정 시작
            List<Font> fontList = new ArrayList<>();
            fontList.add(new Font("", Font.HANGING_BASELINE, fontsize));
            fontList.add(new Font("Courier", Font.ITALIC, fontsize));
            fontList.add(new Font("", Font.PLAIN, fontsize));

            List<Color> colorList = new ArrayList<>();
            colorList.add(Color.BLACK);
            //폰트설정 종료

            //Captcha 이미지 생성 시작
            Captcha captcha = new Captcha.Builder(width, height)
                    .addText(new NumbersAnswerProducer(6)
                            , new DefaultWordRenderer(colorList, fontList))
                    .gimp(new DropShadowGimpyRenderer()).gimp()
                    .addNoise().addNoise().addBorder()
                    .addBackground(new GradiatedBackgroundProducer())
                    .build();
            //Captcha 이미지 생성 종료

            //심플캡챠 저장
            request.getSession().setAttribute("captcha", captcha);

            //Captcha 이미지 저장 -- jdk 버전이 안맞아 사용 불가
//            CaptchaServletUtil.writeImage((OutputStream) response, captcha.getImage());
            CaptchaServletUtil.writeImage(response, captcha.getImage());
        }catch (Exception e){
            log.error("SimpleCaptcha 생성 실패 : {}", e.getMessage());
        }
    }

    /**
     * 구글 reCaptcha 검증 로직
     * @param token 검증을 위해 생성되는 토큰
     * @param ip 요청한 클라이언트 IP
     * @return 응답 객체 반환
     */
    public CaptchaResponse checkReCaptcha(String token, String ip) {
        if (token.equals("")){
            log.error("토큰이 없음 : token={}, ip={}", token, ip);
            return new CaptchaResponse("FAIL", "토큰이 없습니다.", false);
        }
        // 요청
        ReCaptchaJsonObject object = requestReCaptchaValidate(token, ip);

        if (object.getSuccess()){
            if (object.getScore() >= ReCaptchaUtil.SCORE){
                log.debug("reCaptcha 통과 : score={}", object.getScore());
                return new CaptchaResponse("OK", "검증 성공", true);
            }else {
                log.error("reCaptcha 점수 미달 : score={}, baseScore={}", object.getScore(), ReCaptchaUtil.SCORE);
                return new CaptchaResponse("FAIL", "점수 미달로 통과하지 못했습니다.", false);
            }
        }else {
            return new CaptchaResponse("FAIL", "요청에 실패했습니다.", false);
        }
    }

    /**
     * 구글 reCaptcha 검증 요청
     * @param token 검증용 토큰
     * @param ip 요청한 클라이언트 IP
     * @return 응답 파싱 후 객체로 반환
     */
    private ReCaptchaJsonObject requestReCaptchaValidate(String token, String ip) {
        ReCaptchaJsonObject object = null;
        RestTemplate restTemplate = new RestTemplate();
        String queryParam = "?secret=" + ReCaptchaUtil.SECRET_KEY + "&response=" + token + "&remoteip=" + ip;

        try {
            object = restTemplate.postForObject(ReCaptchaUtil.URL + queryParam, null, ReCaptchaJsonObject.class);
        }catch (RestClientException e){
            log.error("요청 실패 : message={}", e.getMessage());
            object = new ReCaptchaJsonObject(false, 0, "요청 실패");
        }

        return object;
    }

    /**
     * SimpleCaptcha 검증 로직
     * @param answer 사용자가 입력한 SimpleCaptcha 번호
     * @param request 세션에서 captcha 를 가져오기 위해 사용
     * @return 응답 객체 반환
     */
    public CaptchaResponse checkSimpleCaptcha(String answer, HttpServletRequest request) {
        if (answer.equals("")){
            log.error("SimpleCaptcha 번호를 입력하지 않음");
            return new CaptchaResponse("FAIL", "captcha 번호를 입력하세요.", false);
        }

        // 세션에서 captcha 객체 가져오기
        Captcha captcha = (Captcha) request.getSession().getAttribute("captcha");

        if (captcha != null){
            if (captcha.isCorrect(answer)){
                log.debug("SimpleCaptcha 검증 성공 : userAnswer={}", answer);
                return new CaptchaResponse("OK", "SimpleCaptcha 검증 완료", true);
            }else {
                // 검증 실패 시 로깅
                log.error("SimpleCaptcha 검증 실패 : userAnswer={}, captchaAnswer={}", answer, captcha.getAnswer());
                return new CaptchaResponse("FAIL", "번호를 다시 확인해주세요.", false);
            }
        }else {
            // 검증 시도 실패 시 로깅
            log.error("SimpleCaptcha 검증 시도 실패 : userAnswer={}", answer);
            return new CaptchaResponse("FAIL", "오류가 발생했습니다. 관리자에게 문의하시기 바랍니다.", false);
        }
    }
}
