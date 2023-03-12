package ru.shothandTelegrambot.bot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Utils {

    public static boolean fWaitKeySession = false;
    public static Map<String, String> mapStatusService = Map.of(
            "ACTIVE", "Сервис активен и доступен",
            "FLUSH", "Очередь к сервису занята",
            "INACTIVE", "Сервис не активен");
    public static Map<String, String> mapTaskState = Map.of(
            "ACTIVE", "В процессе",
            "INQUEUE", "В очереди",
            "READY", "Готова",
            "FAULT", "Завершена с ошибкой",
            "NOTFOUND", "Не найдена",
            "ADDED", "Добавлена");
    public static ArrayList<KeySession> KeySessions = new ArrayList<>();
    public static CopyOnWriteArrayList<TasksByChat> TasksByChats = new CopyOnWriteArrayList<>();


    private static KeySession findKeyByIdChat(long idChat) {

        return KeySessions.stream()
                .filter(it -> it.idChat == idChat)
                .findAny()
                .orElse(null);

    }


    public static String getKeyByIdChat(long idChat) {
        KeySession keyFromArray = findKeyByIdChat(idChat);

        if (keyFromArray == null) return null;
        else return keyFromArray.key;
    }

    public static void addOrUpdateKey(String key, long idChat) {
        KeySession keyFromArray = findKeyByIdChat(idChat);

        if (keyFromArray == null)
            KeySessions.add(new KeySession(idChat, key));
        else
            keyFromArray.key = key;
    }


    private static TasksByChat findObjTaskByIdChat(long idChat) {

        return TasksByChats.stream()
                .filter(it -> it.idChat == idChat)
                .findAny()
                .orElse(null);

    }

    public static CopyOnWriteArrayList<TasksByChat> getTasksByChats() {
        return TasksByChats;
    }


    public static void AddTaskByChat(long idChat, int idMessage, long numTask, boolean decrypt) {
        TasksByChats.add(new TasksByChat(idChat, idMessage, numTask, decrypt, "ADDED"));
    }

    public static String getStatusesTasksByChat(long idChat) {
        ArrayList<TasksByChat> tasksList = findObjTasksByIdChat(idChat);
        StringBuilder tasks = new StringBuilder();
        for (TasksByChat item : tasksList) {
            tasks.append(String.valueOf(Utils.getTaskNumInChat(item.idChat, item.numTask))).append(" - ").append(Utils.mapTaskState.get(item.state)).append('\n');
        }
        if (tasksList.isEmpty())
            tasks.append("Активных задач нет").append('\n');
        return tasks.toString();
    }

    public static ArrayList<TasksByChat> findObjTasksByIdChat(long idChat) {
        return TasksByChats.stream().filter(it -> it.idChat == idChat).collect(Collectors.toCollection(ArrayList::new));
    }

    public static int getTaskNumInChat(long idChat, long numTask) {
        ArrayList<TasksByChat> tasksList = findObjTasksByIdChat(idChat);
        for (TasksByChat item : tasksList) {
            if (item.numTask == numTask)

                return item.numForUser;
        }
        return -1;
    }

    public static void setUserNumForTask(long idChat) {
        ArrayList<TasksByChat> tasksList = findObjTasksByIdChat(idChat);
        for (TasksByChat item : tasksList) {
            if (item.numForUser == -1)
                item.numForUser = tasksList.indexOf(item) + 1;
        }
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
     *
     * @param msg сообщение
     */
    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    /**
     * Формирование имени пользователя. Если заполнен никнейм, используем его. Если нет - используем фамилию и имя
     *
     * @param user пользователь
     */
    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    public static ReplyKeyboardMarkup getKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("/help (Помощь)");
        keyboardFirstRow.add("/startsession (Задать ключ)");

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("/coder (Закодировать)");
        keyboardSecondRow.add("/decoder (Раскодировать)");

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add("/ss (Статус сервиса)");
        keyboardThirdRow.add("/ts (Статус задач)");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }
}