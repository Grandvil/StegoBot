package ru.shothandTelegrambot.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.shothandTelegrambot.httpClient.HttpClient;

import java.util.Arrays;

@Slf4j
public abstract class Command implements IBotCommand {
    HttpClient httpClient;
    private String commandIdentifier=null;
    private String description=null;

    public Command(String commandIdentifier, String description) {
        this.commandIdentifier = commandIdentifier;
        this.description = description;
    }

    @Override
    public String getCommandIdentifier() {
        return commandIdentifier;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void processDocument(AbsSender absSender, SendDocument doc){
        try {
            absSender.execute(doc);
        } catch (TelegramApiException e) {
            log.error(String.format("Command doc processing error: %s", e.getMessage(), e));
        }
    }
    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        log.debug(String.format(String.format("COMMAND: %s(%s)", message.getText(), Arrays.toString(strings))));
        try {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(message.getChatId().toString())
                    .text(message.getText())
                    .build();
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(String.format("Command message processing error: %s", e.getMessage(), e));
        }
    }
}
