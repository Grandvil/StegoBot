package ru.shothandTelegrambot.bot;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import ru.shothandTelegrambot.bot.commands.CommandHelp;
import ru.shothandTelegrambot.bot.commands.CommandStart;
import ru.shothandTelegrambot.bot.commands.CommandCoder;
import ru.shothandTelegrambot.bot.commands.CommandDecoder;

import ru.shothandTelegrambot.bot.commands.CommandServiceStatus;
import ru.shothandTelegrambot.bot.commands.CommandStartSession;
import ru.shothandTelegrambot.bot.commands.CommandTaskStatus;
import ru.shothandTelegrambot.core.exceptions.UserException;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.shothandTelegrambot.httpClient.HttpClient;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Slf4j
public class BotProcessor extends TelegramLongPollingCommandBot {

    HttpClient httpClient = new HttpClient();
    private final static int TEXT_LIMIT = 512;
    private final static BotSettings botSettings = BotSettings.getInstance();
    private static BotProcessor instance;
    private final TelegramBotsApi telegramBotsApi;
    private List<String> registeredCommands = new ArrayList<>();

    Thread threadSenderResult;
    public boolean threadSenderResultFlagRun = true;

    void threadSenderResultStart() {
        threadSenderResult = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClientTh = new HttpClient();
                    while (threadSenderResultFlagRun) {
                        try {
                            CopyOnWriteArrayList<TasksByChat> TaskByChats = Utils.getTasksByChats();
                            for (TasksByChat task : TaskByChats) {
                                if (task.state.equals("READY")) {
                                    JSONObject jsonAnswer = httpClientTh.getFileFromCoderService(String.valueOf(task.numTask));
                                    if (task.decrypt) {
                                        try {
                                            String txt = jsonAnswer.getString("text");
                                            if (txt != null) {
                                                sendMessage(task.idChat, task.idMessage, "Номер задачи: " +
                                                        String.valueOf(Utils.getTaskNumInChat(task.idChat, task.numTask)) + " \nРасшифрованный текст: " + txt);
                                                TaskByChats.remove(task);
                                                continue;
                                            }
                                            sendMessage(task.idChat, task.idMessage, "Номер задачи:" +
                                                    String.valueOf(Utils.getTaskNumInChat(task.idChat, task.numTask)) + " \nДекодированный текст пуст!");
                                            TaskByChats.remove(task);
                                            continue;
                                        } catch (JSONException ignored) {
                                            TaskByChats.remove(task);
                                        }
                                    } else {
                                        BufferedImage img = Utils.base64StringToImg(jsonAnswer.getString("image"));

                                        if (img == null) {
                                            sendMessage(task.idChat, task.idMessage, "Номер задачи:" +
                                                    String.valueOf(Utils.getTaskNumInChat(task.idChat, task.numTask)) + " \nНет данных в изображении");
                                            TaskByChats.remove(task);
                                            continue;
                                        }
                                        try {
                                            File result = File.createTempFile(String.valueOf(task.idChat) + String.valueOf(task.numTask), ".png");
                                            FileOutputStream out = new FileOutputStream(result);

                                            ImageIO.write(img, "png", out);
                                            SendDocument doc = new SendDocument();
                                            doc.setChatId(String.valueOf(task.idChat));
                                            doc.setReplyToMessageId(task.idMessage);
                                            doc.setDocument(new InputFile(result, String.format("%s.png", String.valueOf(task.idChat) +
                                                    String.valueOf(task.numTask))));
                                            doc.setCaption(String.valueOf(Utils.getTaskNumInChat(task.idChat, task.numTask)));

                                            execute(doc); //отсылаем
                                            TaskByChats.remove(task);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            sendMessage(task.idChat, task.idMessage, "Номер задачи:" +
                                                    String.valueOf(Utils.getTaskNumInChat(task.idChat, task.numTask)) + " \nОшибка отсылки документа");
                                            TaskByChats.remove(task);
                                        }
                                    }
                                } else { //обновить статус задачи
                                    if (task.state.equals("FAULT") || task.state.equals("NOTFOUND")) {
                                        sendMessage(task.idChat, task.idMessage, "Номер задачи:" +
                                                String.valueOf(Utils.getTaskNumInChat(task.idChat, task.numTask)) + " \nОшибка задачи или не найдена");
                                        TaskByChats.remove(task);
                                        continue;
                                    }
                                    task.state = httpClientTh.getTaskStateService(String.valueOf(task.numTask));
                                }

                            }
                        } catch (Exception ignored) {
                        }
                        ;
                        Thread.sleep(100); //не быстрее 100мс поток крутится
                    }
                } catch (Exception e) {
                    System.out.println("threadSenderResult exception: " + e.toString());
                    e.printStackTrace();
                }
            }
        });
        threadSenderResult.start();
    }

    public void sendMessage(Long chatId, String message) {
        try {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .replyMarkup(Utils.getKeyboard())
                    .build();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(String.format("Sending message error: %s", e.getMessage()));
        }
    }

    public void sendMessage(Long chatId, int messageId, String message) {
        try {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(chatId.toString())
                    .replyToMessageId(messageId)
                    .text(message)
                    .replyMarkup(Utils.getKeyboard())
                    .build();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(String.format("Sending message error: %s", e.getMessage()));
        }
    }

    public void sendImage(Long chatId, String path) throws UserException {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setPhoto(new InputFile(new File(path)));
            photo.setChatId(chatId.toString());
            execute(photo);
        } catch (TelegramApiException e) {
            log.error(String.format("Sending image error: %s", e.getMessage()));
            throw new UserException("Ошибка отправки изображения");
        }
    }

    @Override
    public String getBotUsername() {
        return botSettings.getUserName();
    }

    @Override
    protected void processInvalidCommandUpdate(Update update) {
        String command = update.getMessage().getText().substring(1);
        sendMessage(
                update.getMessage().getChatId()
                , String.format("Некорректная команда [%s], доступные команды: %s"
                        , command
                        , registeredCommands.toString()));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            try {
                MessageType messageType = getMessageType(update);
                switch (messageType) {
                    case COMMAND:
                        processInvalidCommandUpdate(update);
                        break;
                    case IMAGE:
                        processImage(update);
                        break;
                    case TEXT:
                        processText(update);
                        break;
                    case DOCUMENT:
                        processDocument(update);
                        break;
                }
            } catch (UserException e) {
                sendMessage(update.getMessage().getChatId(), e.getMessage());
            } catch (TelegramApiException | RuntimeException | IOException e) {
                log.error(String.format("Received message processing error: %s", e.getMessage()));
                sendMessage(update.getMessage().getChatId(), "Ошибка обработки сообщения");
            }
        }
    }

    @Override
    public String getBotToken() {
        return botSettings.getToken();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    private void processText(Update update) throws TelegramApiException, IOException, UserException {
        if (Utils.fWaitKeySession) {
            Utils.fWaitKeySession = false;
            Utils.addOrUpdateKey(update.getMessage().getText(), update.getMessage().getChatId());
            sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(),
                    "Ключ добавлен! Для запуска кодирования воспользуйтесь командой /coder, для декодирования - /decoder");
            return;
        }
        sendMessage(update.getMessage().getChatId(), "Ошибка команды. Пришлите /help");
    }

    private MessageType getMessageType(Update update) throws UserException {
        MessageType messageType = null;
        try {
            if (update.getMessage().getPhoto() != null)
                return MessageType.IMAGE;
            if (update.getMessage().getText() != null)
                return (update.getMessage().getText().matches("^/[\\w]*$")) ?
                        MessageType.COMMAND :
                        MessageType.TEXT;
            if (update.getMessage().getDocument() != null)
                return MessageType.DOCUMENT;

            throw new IllegalArgumentException(update.toString());

        } catch (RuntimeException e) {
            log.error(String.format("Invalid message type: %s", e.getMessage()));
            throw new UserException("Неподдерживаемый тип сообщения");
        }
    }

    private void processDocument(Update update) throws TelegramApiException, IOException, UserException {
        logMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId(), true, "$image");

        try {
            String messageStr = update.getMessage().getCaption();

            if (!update.getMessage().getDocument().getMimeType().equals("image/png")) {
                sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Поддерживаются только изображения PNG");
                return;
            }
            if (Utils.getKeyByIdChat(update.getMessage().getChatId()) == null) {
                sendMessage(update.getMessage().getChatId(), "Ключ сессии не задан! Задайте ключ командой /startsession <ключ>");
                return;
            }

            String fileUrl = getFileUrl(update.getMessage().getDocument().getFileId());
            String answer = null;
            try {
                answer = httpClient.sendFileToCoderService(Utils.imgToBase64String(ImageIO.read(new URL(fileUrl)), "png"),
                        Utils.getKeyByIdChat(update.getMessage().getChatId()), null, false);

                String mess = "";
                if (answer != null) {
                    JSONObject jsonObj = new JSONObject(answer);
                    Utils.AddTaskByChat(update.getMessage().getChatId(), update.getMessage().getMessageId(), Long.parseLong(jsonObj.getString("numTask")), true);
                    Utils.setUserNumForTask(update.getMessage().getChatId());
                    mess = "Номер задачи: " +
                            String.valueOf(Utils.getTaskNumInChat(update.getMessage().getChatId(), Long.parseLong(jsonObj.getString("numTask"))))
                            + ". Среднее время ожидания результата: 30 секунд";
                } else
                    mess = "Ошибка сервиса";

                sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), mess);
            } catch (Exception e) {
                sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Ошибка сервиса");
                return;
            }

        } catch (Exception e) {
            sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Ошибка команды");
        }
    }

    private void processImage(Update update) throws TelegramApiException, IOException, UserException {
        logMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId(), true, "$image");

        try {
            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
            String fileUrl = getFileUrl(update.getMessage().getPhoto().get(photoSizes.size() - 1).getFileId());

            String messageStr = update.getMessage().getCaption();


            if (messageStr.isEmpty()) {
                sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Ошибка команды - нет текста для кодирования");
                return;
            }
            if (Utils.getKeyByIdChat(update.getMessage().getChatId()) == null) {
                sendMessage(update.getMessage().getChatId(), "Ключ сессии не задан! Задайте ключ командой /startsession <ключ>");
                return;
            }
            String answer = null;
            try {
                answer = httpClient.sendFileToCoderService(Utils.imgToBase64String(ImageIO.read(new URL(fileUrl)), "png"),
                        Utils.getKeyByIdChat(update.getMessage().getChatId()), messageStr, true);
                String mess = "";
                if (answer != null) {
                    JSONObject jsonObj = new JSONObject(answer);

                    Utils.AddTaskByChat(update.getMessage().getChatId(), update.getMessage().getMessageId(), Long.parseLong(jsonObj.getString("numTask")), false);
                    Utils.setUserNumForTask(update.getMessage().getChatId());

                    mess = "Номер задачи: " +
                            String.valueOf(Utils.getTaskNumInChat(update.getMessage().getChatId(), Long.parseLong(jsonObj.getString("numTask"))))
                            + ". Среднее время ожидания результата: 30 секунд";
                } else
                    mess = "Ошибка сервиса";

                sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), mess);
            } catch (Exception e) {
                sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Ошибка сервиса");
                return;
            }

        } catch (Exception e) {
            sendMessage(update.getMessage().getChatId(), "Ошибка команды");
        }
    }

    private JSONObject getFileRequest(String fileId) throws IOException {
        String fileUrl = String.format("https://api.telegram.org/bot%s/getFile?file_id=%s",
                botSettings.getToken(),
                fileId);
        return IOTools.readJsonFromUrl(fileUrl);
    }

    public String getFileUrl(String fileId) throws IOException {
        JSONObject jsonObject = getFileRequest(fileId);
        return String.format("https://api.telegram.org/file/bot%s/%s",
                botSettings.getToken(),
                jsonObject.get("file_path"));
    }

    private void logMessage(Long chatId, Long userId, boolean input, String text) {
        if (text.length() > TEXT_LIMIT)
            text = text.substring(0, TEXT_LIMIT);
        log.info(String.format("CHAT [%d] MESSAGE %s %d: %s", chatId, input ? "FROM" : "TO", userId, text));
    }

    private void setRegisteredCommands() {
        registeredCommands = getRegisteredCommands()
                .stream()
                .map(IBotCommand::getCommandIdentifier)
                .collect(Collectors.toList());
    }

    private void registerCommands() {
        register(new CommandStart());
        register(new CommandHelp());
        register(new CommandStartSession());
        register(new CommandCoder());
        register(new CommandDecoder());

        register(new CommandServiceStatus(httpClient));
        register(new CommandTaskStatus());
        setRegisteredCommands();
    }

    public void registerBot() {
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Telegram API initialization error: " + e.getMessage());
        }
    }

    {
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            registerBot();
            registerCommands();
        } catch (TelegramApiException e) {
            throw new RuntimeException("Telegram Bot initialization error: " + e.getMessage());
        }
    }

    public static BotProcessor getInstance() {
        if (instance == null)
            instance = new BotProcessor();
        return instance;
    }

    public BotProcessor() {
        super();
        threadSenderResultStart();
    }
}
