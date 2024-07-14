package test.recaptcha.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SimpleCaptchaInfo {

    private int width;
    private int height;
    private int fontsize;

    public SimpleCaptchaInfo() {
        this.width = 120;
        this.height = 35;
        this.fontsize = 35;
    }

    public SimpleCaptchaInfo(int width, int height, int fontsize) {
        this.width = width;
        this.height = height;
        this.fontsize = fontsize;
    }
}
