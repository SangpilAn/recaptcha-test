package test.recaptcha.util;

import jakarta.servlet.http.HttpServletResponse;
import nl.captcha.audio.Sample;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CaptchaServletUtil {

    public CaptchaServletUtil() {
    }

    public static void writeImage(HttpServletResponse response, BufferedImage bi) {
        response.setHeader("Cache-Control", "private,no-cache,no-store");
        response.setContentType("image/png");

        try {
            writeImage((OutputStream)response.getOutputStream(), bi);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static void writeImage(OutputStream os, BufferedImage bi) {
        try {
            ImageIO.write(bi, "png", os);
            os.close();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static void writeAudio(HttpServletResponse response, Sample sample) {
        response.setHeader("Cache-Control", "private,no-cache,no-store");
        response.setContentType("audio/wave");

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            AudioSystem.write(sample.getAudioInputStream(), AudioFileFormat.Type.WAVE, baos);
            response.setContentLength(baos.size());
            OutputStream os = response.getOutputStream();
            os.write(baos.toByteArray());
            os.flush();
            os.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

}
