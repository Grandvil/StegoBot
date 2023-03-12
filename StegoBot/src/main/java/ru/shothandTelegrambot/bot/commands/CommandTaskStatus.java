package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.shothandTelegrambot.bot.Utils;


public class CommandTaskStatus extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText(Utils.getStatusesTasksByChat(message.getChatId()));
        super.processMessage(absSender, message, null);
    }

    public CommandTaskStatus() {
        super("ts", "Статус задачи");
    }

}