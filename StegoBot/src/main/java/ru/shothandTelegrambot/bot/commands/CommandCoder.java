package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandCoder extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {

        message.setText("Теперь пришлите в одном сообщении изображение с подписью, содержащую текст для кодирования");
        super.processMessage(absSender, message, null);
    }

    public CommandCoder() {
        super("coder", "Запуск задачи кодирования");
    }

}