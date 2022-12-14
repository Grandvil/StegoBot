package ru.shothandyelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandTaskStatus extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("TODO: статус задачи");
        super.processMessage(absSender, message, null);
    }

    public CommandTaskStatus() {
        super("ts", "Статус задачи");
    }

}
