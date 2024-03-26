package test.recaptcha.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReCaptchaUtil {
    // properties 로 관리할 예정
    public final String URL = "https://www.google.com/recaptcha/api/siteverify";
    public final String SECRET_KEY = "6Lca15MpAAAAAASCChhnZzy608hlb9CmlVWJ-18w";
    public final Double SCORE = 0.5;

    private final Map<String, String> errorMap;

    public ReCaptchaUtil() {
        this.errorMap = initErrorMap();
    }

    /**
     * Google reCaptcha API 에러코드를 정리하여 Map 에 Message 를 담음
     * @return code, message 를 가지는 Map 반환
     */
    private Map<String, String> initErrorMap() {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("missing-input-secret","비밀키가 없습니다.");
        errorMap.put("invalid-input-secret","비밀키가 일치하지 않습니다.");
        errorMap.put("missing-input-response","토큰이 없습니다.");
        errorMap.put("invalid-input-response","토큰 형식이 잘못되었습니다.");
        errorMap.put("bad-request","요청 형식이 잘못되었습니다.");
        errorMap.put("timeout-or-duplicate","오래되었거나 사용된 토큰입니다.");
        return errorMap;
    }

    public List<String> errorCheck(String[] errors){
        List<String> messages = new ArrayList<>();

        for (String error : errors) {
            if (errorMap.containsKey(error)){
                messages.add(errorMap.get(error));
            }
        }

        return messages;
    }

}
