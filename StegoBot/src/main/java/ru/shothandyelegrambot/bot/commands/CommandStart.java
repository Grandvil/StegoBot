package ru.shothandyelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandStart extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("Добро пожаловать! \n"+
                "Если Вам нужна помощь, нажмите /help \n\n"+
                 "Для начала сессии введите команду /startsession ваш_ключ-пароль (через пробел)");
        super.processMessage(absSender, message, null);
    }

    public CommandStart() {
        super("start", "Запуск бота");
    }

}
