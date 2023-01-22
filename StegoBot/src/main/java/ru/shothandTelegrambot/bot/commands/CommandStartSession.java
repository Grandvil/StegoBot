package ru.shothandTelegrambot.bot.commands;


import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.shothandTelegrambot.bot.Utils;

public class CommandStartSession extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        Utils.addOrUpdateKey(strings[0], message.getChatId());
        message.setText("Ключ добавлен! Для запуска кодирования воспользуйтесь командой /coder вместе с текстом для шифрования и изображением.\n" +
                "Пример: /coder моё_сообщение (вместе с командой необходимо прикрепить изображение в одном сообщении)");
        super.processMessage(absSender, message, null);
    }

    public CommandStartSession() {
        super("startsession", "Запуск сессии");
    }

}
