package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandDecoder extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("Теперь пришлите ДОКУМЕНТ (убрать галочку с \"Сжать изображение\" на компьютере или отправить изображение ФАЙЛОМ с телефона) в формате PNG для расшифровки \n\n" +
                "ВНИМАНИЕ! Чтобы правильно декодировать послание в документе, ключ сессии у Вас должен быть такой же, как у кодировщика этого документа \n");
        super.processMessage(absSender, message, null);
    }

    public CommandDecoder() {
        super("decoder", "Запуск задачи декодирования");
    }

}