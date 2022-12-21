package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.shothandTelegrambot.bot.Utils;
import ru.shothandTelegrambot.httpClient.HttpClient;

import java.util.Arrays;

public class CommandTaskStatus extends Command {
    HttpClient httpClient;

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        //String numTask=strings[0];
        if (!strings[0].isEmpty())
            message.setText(Utils.mapTaskState.get(httpClient.getTaskStateService(strings[0])));
        else
            message.setText("Укажите номер задачи");
        super.processMessage(absSender, message, null);
    }

    public CommandTaskStatus(HttpClient httpClient) {
        super("ts", "Статус задачи");
        this.httpClient=httpClient;
    }

}
