package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;


public class CommandHelp extends Command {

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        message.setText("❗*Список команд*\n" +
                "/start - начало работы с ботом\n" +
                "/startsession мой_ключ - начало сессии с использованием стегоключа (команду вводите вручную, написав через пробел ключ)\n" +
                "Пример: /startsession 123\n" +
                "/ss - статус сервиса\n" +
                "/ts номер_задачи - статус задачи\n" +
                "Пример: /ts 1\n" +
                "/coder - кодирование текста в прикрепленное к сообщению изображение\n" +
                "Пример: /coder \"моё сообщение\" (вместе с командой необходимо прикрепить изображение в одном сообщении)\n" +
                "/sr номер_задачи - прислать результат (если доступен)\n" +
                "Пример: /sr 1\n" +
                "/decoder вместе с прикреплённым изображением (изображение нужно прикрепить в виде документа) - прислать зашифрованное сообщение\n" +
                "/help - помощь\n\n");
        super.processMessage(absSender, message, strings);
    }

    public CommandHelp() {
        super("help", "Справка \\help \n");
    }

}
