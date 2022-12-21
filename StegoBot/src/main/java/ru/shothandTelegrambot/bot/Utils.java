package ru.shothandTelegrambot.bot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Map<String, String> mapStatusService =  Map.of(
            "ACTIVE","Сервис активен и доступен",
            "FLUSH","Очередь к сервису занята",
            "INACTIVE","Сервис не активен");
    public static Map<String, String> mapTaskState =  Map.of(
            "ACTIVE","В процессе",
            "INQUEUE","В очереди",
            "READY","Готова",
            "FAULT","Завершена с ошибкой",
            "NOTFOUND","Не найдена");
    public static ArrayList<KeySession> KeySessions=new ArrayList<>();


    //get KeySession obj by idChat
    private static KeySession findKeyByIdChat(long idChat) {
        //find key in arraylist, j8+ need
        return KeySessions.stream()
                .filter(it -> it.idChat==idChat)
                .findAny()
                .orElse(null);

    }

    //get String key by idChat
    public static String getKeyByIdChat(long idChat) {
        KeySession keyFromArray=findKeyByIdChat(idChat);

        if (keyFromArray==null) return null;
        else return keyFromArray.key;
    }

    //wtf?! reading the name of method
    public static void addOrUpdateKey(String key, long idChat) {
        KeySession keyFromArray=findKeyByIdChat(idChat);

        if (keyFromArray==null)
            KeySessions.add(new KeySession(idChat,key)); //add new chatid&key to array
        else
            keyFromArray.key=key;
    }

    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (final OutputStream b64os = Base64.getUrlEncoder().wrap(os)) {
            ImageIO.write(img, formatName, b64os);
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return os.toString();
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getUrlDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
    /**
     * Формирование имени пользователя
     * @param msg сообщение
     */
    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    /**
     * Формирование имени пользователя. Если заполнен никнейм, используем его. Если нет - используем фамилию и имя
     * @param user пользователь
     */
    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }
}
