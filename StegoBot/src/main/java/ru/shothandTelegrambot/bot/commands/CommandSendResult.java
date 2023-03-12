package ru.shothandTelegrambot.bot.commands;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.shothandTelegrambot.bot.Utils;
import ru.shothandTelegrambot.httpClient.HttpClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class CommandSendResult extends Command {
    HttpClient httpClient;

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        if (strings[0].isEmpty()) {
            message.setText("Укажите номер задачи");
            super.processMessage(absSender, message, null);
            return;
        }
        JSONObject jsonAnswer = httpClient.getFileFromCoderService(strings[0]);
        try {
            String txt = jsonAnswer.getString("text");
            if (txt != null) {
                message.setText("Расшифрованный текст: " + txt);
                super.processMessage(absSender, message, null);
                return;
            }
            message.setText("Декодированный текст пуст!");
            super.processMessage(absSender, message, null);
            return;
        } catch (JSONException ignored) {
        }

        BufferedImage img = Utils.base64StringToImg(jsonAnswer.getString("image"));

        if (img == null) {
            message.setText("Нет данных в изображении");
            super.processMessage(absSender, message, null);
            return;
        }
        try {
            File result = File.createTempFile(message.getChatId().toString() + strings[0], ".png");
            FileOutputStream out = new FileOutputStream(result);
            ImageIO.write(img, "png", out);
            SendDocument doc = new SendDocument();
            doc.setChatId(message.getChatId().toString());
            doc.setDocument(new InputFile(result, String.format("%s.png", message.getChatId().toString() + strings[0])));

            super.processDocument(absSender, doc);

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Ошибка отсылки документа");
            super.processMessage(absSender, message, null);
        }
    }

    public CommandSendResult(HttpClient httpClient) {
        super("sr", "Прислать результат");
        this.httpClient = httpClient;
    }

}