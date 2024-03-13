package test.recaptcha.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;


@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
public class ReCaptchaJsonObject {

    private Boolean success;
    private double score;
    @JsonProperty("error-codes")
    private String errorCodes;

    public ReCaptchaJsonObject(Boolean success, double score, String errorCodes) {
        this.success = success;
        this.score = score;
        this.errorCodes = errorCodes;
    }
}
