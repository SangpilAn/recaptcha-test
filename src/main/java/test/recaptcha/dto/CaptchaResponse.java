package test.recaptcha.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaptchaResponse {
    private String status;
    private String message;
    private String action;

    public CaptchaResponse(String status, String message, boolean pass) {
        this.status = status;
        this.message = message;
        if (pass){
            // login.fcc 를 검증 성공 시 주입
            action = "http://localhost:8080/login";
        }else {
            action = "";
        }
    }
}
