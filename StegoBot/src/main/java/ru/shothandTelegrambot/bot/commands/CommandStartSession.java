package ru.shothandTelegrambot.bot.commands;


import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.shothandTelegrambot.bot.Utils;

public class CommandStartSession extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        Utils.fWaitKeySession = true;
        message.setText("Теперь задайте ключ шифрования для текущей сессии");
        super.processMessage(absSender, message, null);
    }

    public CommandStartSession() {
        super("startsession", "Запуск сессии");
    }

}