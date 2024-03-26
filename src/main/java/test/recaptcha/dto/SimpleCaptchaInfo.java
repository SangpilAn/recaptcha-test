package test.recaptcha.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SimpleCaptchaInfo {

    @Builder.Default
    private int width = 120;
    @Builder.Default
    private int height = 35;
    @Builder.Default
    private int fontsize = 35;

    public SimpleCaptchaInfo(int width, int height, int fontsize) {
        this.width = width;
        this.height = height;
        this.fontsize = fontsize;
    }
}
