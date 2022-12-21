package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.shothandTelegrambot.bot.Utils;
import ru.shothandTelegrambot.httpClient.HttpClient;

public class CommandServiceStatus extends Command {
    HttpClient httpClient;

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText(Utils.mapStatusService.get(httpClient.getStatusService()));
        super.processMessage(absSender, message, null);
    }

    public CommandServiceStatus(HttpClient httpClient) {
        super("ss", "Статус сервиса");
        this.httpClient=httpClient;
    }

}
