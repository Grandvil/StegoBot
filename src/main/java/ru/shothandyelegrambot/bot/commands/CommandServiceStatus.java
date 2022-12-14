package ru.shothandyelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandServiceStatus extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("TODO: статус сервиса");
        super.processMessage(absSender, message, null);
    }

    public CommandServiceStatus() {
        super("ss", "Статус сервиса");
    }

}
