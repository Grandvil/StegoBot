package ru.Shorthand;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Base64;

public class StegoServiceUtils {
    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (final OutputStream b64os = Base64.getUrlEncoder().wrap(os)) {
            ImageIO.write(img, formatName, b64os);
        } catch (final IOException ioe) {
            System.out.println("imgToBase64String exception:" + ioe.toString());
            throw new UncheckedIOException(ioe);
        }
        return os.toString();
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getUrlDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            System.out.println("base64StringToImg exception:" + ioe.toString());
            throw new UncheckedIOException(ioe);
        }
    }
}