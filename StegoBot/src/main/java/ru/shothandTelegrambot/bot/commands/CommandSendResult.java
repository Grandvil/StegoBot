package ru.shothandTelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.shothandTelegrambot.httpClient.HttpClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class CommandSendResult extends Command {
    HttpClient httpClient;

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        BufferedImage img=httpClient.getFileFromCoderService(strings[0]);
        if (img==null){
            message.setText("Нет данных в изображении");
            super.processMessage(absSender, message, null);
            return;
        }
        try
        {
        File result = File.createTempFile(message.getChatId().toString()+strings[0], ".jpg");
        FileOutputStream out = new FileOutputStream(result);
        //File outputfile = new File("image.jpg");
        //ImageIO.write(bufferedImage, "jpg", outputfile);
        ImageIO.write(img,"jpg",out);
        SendDocument doc = new SendDocument();
        doc.setChatId(message.getChatId().toString());
        doc.setDocument(new InputFile(result, String.format("%s.jpg", message.getChatId().toString()+strings[0])));

        super.processDocument(absSender,doc);

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Ошибка отсылки документа");
            super.processMessage(absSender, message, null);
        }
    }

    public CommandSendResult(HttpClient httpClient) {
        super("sr", "Прислать результат");
        this.httpClient=httpClient;
    }

}