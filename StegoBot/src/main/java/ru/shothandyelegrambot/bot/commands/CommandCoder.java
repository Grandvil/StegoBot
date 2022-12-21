package ru.shothandyelegrambot.bot.commands;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.shothandyelegrambot.bot.BotProcessor;
import ru.shothandyelegrambot.bot.IOTools;

import java.io.IOException;
import java.util.List;

import static org.telegram.telegrambots.meta.api.objects.File.getFileUrl;

public class CommandCoder extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {

        try {
            if (message.getPhoto()!=null) {
                List<PhotoSize> photoSizes = message.getPhoto();
                String fileUrl = BotProcessor.getInstance().getFileUrl(message.getPhoto().get(photoSizes.size() - 1).getFileId());
            }
            else {
                message.setText("Ошибка команды - нет прикрепленного изображения");
                super.processMessage(absSender, message, null);
                return;
            }
        } catch (Exception e) {
            message.setText("Ошибка команды");
            super.processMessage(absSender, message, null);
            return;
        }

        message.setText("Ожидайте...");
        super.processMessage(absSender, message, null);
    }

    public CommandCoder() {
        super("coder", "Запуск задачи кодирования");
    }

}
