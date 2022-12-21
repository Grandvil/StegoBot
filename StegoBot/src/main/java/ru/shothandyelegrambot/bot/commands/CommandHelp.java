package ru.shothandyelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;


public class CommandHelp extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("Я бот, который поможет Вам зашифровать Ваш текст в изображение формата jpg при помощи стеганографии и стегоключа\n\n" +
                "❗*Список команд*\n" +
                "/start - начало работы с ботом\n" +
                "/startsession мой_ключ - начало сессии с использованием стегоключа (команду вводите вручную, написав через пробел ключ)\n" +
                "/ss - статус сервиса\n" +
                "/ts {id}- статус задачи (номер задачи через пробел после ts)\n" +
                "/coder - кодирование текста в прикрепленное изображение к сообщению\n" +
                "Пример: /coder \"моё сообщение\" (вместе с командой необходимо прикрепить изображение в одном сообщении)\n"+
                "/rs - прислать результат повторно (если доступен)\n" +
                "/help - помощь\n\n" );
        super.processMessage(absSender, message, strings);
    }

    public CommandHelp() {
        super("help", "Справка \\help \n");
    }

}
