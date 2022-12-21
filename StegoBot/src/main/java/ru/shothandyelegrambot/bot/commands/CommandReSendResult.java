package ru.shothandyelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandReSendResult extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("TODO: пересылка результата еще раз");
        super.processMessage(absSender, message, null);
    }

    public CommandReSendResult() {
        super("rs", "Прислать результат еще раз");
    }

}