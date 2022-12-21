package ru.shothandyelegrambot.bot.commands;


import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.shothandyelegrambot.bot.Utils;

public class CommandStartSession extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        Utils.addOrUpdateKey(strings.toString(), message.getChatId());
        message.setText("Ключ добавлен! Для запуска кодирования воспользуйтесь командой /coder");
        super.processMessage(absSender, message, null);
    }

    public CommandStartSession() {
        super("startsession", "Запуск сессии");
    }

}
