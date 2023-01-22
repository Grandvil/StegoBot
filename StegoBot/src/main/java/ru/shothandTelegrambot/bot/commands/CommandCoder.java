package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.shothandTelegrambot.bot.BotProcessor;
import ru.shothandTelegrambot.bot.Utils;
import ru.shothandTelegrambot.httpClient.HttpClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Base64;
public class CommandCoder extends Command {
    HttpClient httpClient;

    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (final OutputStream b64os = Base64.getEncoder().wrap(os)) {
            ImageIO.write(img, formatName, b64os);
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return os.toString();
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {

        try {
            String fileUrl =null;
            if (message.getPhoto()!=null) {
                List<PhotoSize> photoSizes = message.getPhoto();
                fileUrl = BotProcessor.getInstance().getFileUrl(message.getPhoto().get(photoSizes.size() - 1).getFileId());
            }
            else {
                message.setText("Ошибка команды - нет прикрепленного изображения");
                super.processMessage(absSender, message, null);
                return;
            }

            if (strings[0].isEmpty()){
                message.setText("Ошибка команды - нет текста для кодирования");
                super.processMessage(absSender, message, null);
                return;
            }
            httpClient.sendFileToCoderService(imgToBase64String(ImageIO.read(new URL(fileUrl)),"jpg"),
                    Utils.getKeyByIdChat(message.getChatId()), Arrays.toString(strings),true);
        } catch (Exception e) {
            message.setText("Ошибка команды");
            super.processMessage(absSender, message, null);
            return;
        }

        message.setText("Ожидайте...");
        super.processMessage(absSender, message, null);
    }

    public CommandCoder(HttpClient httpClient) {
        super("coder", "Запуск задачи кодирования");
        this.httpClient=httpClient;
    }

}
