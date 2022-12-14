package ru.shothandyelegrambot.bot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.shothandyelegrambot.bot.KeySession;

import java.util.ArrayList;

public class Utils {

    public static ArrayList<KeySession> KeySessions=new ArrayList<>();

    //get KeySession obj by idChat
    private static KeySession findKeyByIdChat(long idChat) {
        //find key in arraylist, j8+ need
        return KeySessions.stream()
                .filter(it -> it.equals(idChat))
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
