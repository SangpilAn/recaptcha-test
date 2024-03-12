package test.recaptcha.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.DropShadowGimpyRenderer;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import test.recaptcha.util.CaptchaServletUtil;
import test.recaptcha.util.ReCaptchaUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
     * @param token 체크를 위해 생성되는 토큰
     * @return 검증 성공/실패 여부
     */
    public boolean checkReCaptcha(String token) {
        URL url;
        HttpURLConnection connection = null;
        boolean flag = false;

        boolean checkToken = false;
        double score;
        String jsonData = "";
        String queryParam = "secret=" + ReCaptchaUtil.SECRET_KEY + "&response=" + token;

        try {
            // connection 생성
            url = new URL(ReCaptchaUtil.URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(ReCaptchaUtil.TIMEOUT);
            connection.setReadTimeout(ReCaptchaUtil.TIMEOUT);

            // 요청
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(queryParam);
            os.flush();
            os.close();

            // 응답 처리
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            while ((jsonData = br.readLine()) != null){
                sb.append(jsonData);
            }

            // json 파싱
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(sb.toString());
            checkToken = Boolean.parseBoolean(jsonObject.get("success").toString());

            if (checkToken){
                score = Double.parseDouble(jsonObject.get("score").toString());

                if (score >= ReCaptchaUtil.SCORE){
                    log.debug("reCaptcha 통과 : score={}", score);
                    flag = true;
                }else {
                    log.error("스코어가 통과하지 못했습니다. : score={}, baseScore={}", score, ReCaptchaUtil.SCORE);
                }
            }else {
                log.error("토큰 비교에 실패했습니다. : errorCode={}", jsonObject.get("error-codes"));
            }

        }catch (Exception e){
            log.error("reCaptcha 체크 실패 : {}", e.getMessage());
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }

        return flag;
    }

    /**
     * SimpleCaptcha 검증 로직
     * @param answer 사용자가 입력한 SimpleCaptcha 번호
     * @param request 세션에서 captcha 를 가져오기 위해 사용
     * @return 검증 성공/실패 여부
     */
    public boolean checkSimpleCaptcha(String answer, HttpServletRequest request) {
        boolean flag = false;

        Captcha captcha = (Captcha) request.getSession().getAttribute("captcha");

        if (captcha != null && answer != null && !"".equals(answer)){
            if (captcha.isCorrect(answer)){
                flag = true;
            }else {
                // 인증 실패 시 로깅
                log.error("인증 실패 : userAnswer={}, captchaAnswer={}", answer, captcha.getAnswer());
            }
        }else {
            // 인증 시도 실패 시 로깅
            log.error("인증 시도 실패 : userAnswer={}, captcha={}", answer, captcha);
        }

        return flag;
    }
}
