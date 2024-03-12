package test.recaptcha.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaptchaResponse {
    private String status;
    private String message;

    public CaptchaResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
