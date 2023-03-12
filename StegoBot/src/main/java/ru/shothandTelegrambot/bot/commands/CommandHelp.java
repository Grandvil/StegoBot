package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;


public class CommandHelp extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("Я бот, который поможет Вам зашифровать Ваш текст в изображение формата png при помощи стеганографии и стегоключа\n\n" +
                "❗*Список команд*\n" +
                "/start - начало работы с ботом\n" +
                "/startsession - начало сессии с использованием стегоключа\n" +
                "/ss - статус сервиса\n" +
                "/ts - статус задачи/задач\n" +
                "/coder - кодирование текста в прикрепленное к сообщению изображение\n" +
                "/decoder - декодирование текста из прикрепленного документа png\n" +
                "/help - помощь\n\n");
        super.processMessage(absSender, message, strings);
    }

    public CommandHelp() {
        super("help", "Справка \\help \n");
    }

}