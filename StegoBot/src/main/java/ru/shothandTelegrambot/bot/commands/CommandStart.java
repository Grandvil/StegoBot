package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class CommandStart extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("Добро пожаловать! Я бот, который поможет Вам зашифровать Ваш текст в изображение при помощи стеганографии и стегоключа." +
                " Если Вам нужна помощь по перечню доступных команд, нажмите /help \n\n" +
                 "Для начала сессии введите команду /startsession ваш-ключ-пароль (через пробел) \n" +
                "Пример: /startsession 123");
        super.processMessage(absSender, message, null);
    }

    public CommandStart() {
        super("start", "Запуск бота");
    }

}
