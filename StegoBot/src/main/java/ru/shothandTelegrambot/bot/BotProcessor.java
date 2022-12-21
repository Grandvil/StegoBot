package ru.shothandTelegrambot.bot;

import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.shothandTelegrambot.bot.commands.CommandHelp;
import ru.shothandTelegrambot.bot.commands.CommandStart;
import ru.shothandTelegrambot.bot.commands.CommandCoder;
import ru.shothandTelegrambot.bot.commands.CommandSendResult;
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
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class BotProcessor extends TelegramLongPollingCommandBot {

    HttpClient httpClient = new HttpClient();
    private final static int TEXT_LIMIT = 512;
    private final static BotSettings botSettings = BotSettings.getInstance();
    private static BotProcessor instance;
    private final TelegramBotsApi telegramBotsApi;
    private List<String> registeredCommands = new ArrayList<>();

    public void sendMessage(Long chatId, String message) {
        try {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(chatId.toString())
                    .text(message)
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

    public void sendQRImage(Long chatId, String path) throws UserException {
        sendImage(chatId, path);
        File file = new File(path);
        if (!file.delete()) {
            log.error(String.format("File '%s' removing error", path));
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
                }
            } catch (UserException e) {
                sendMessage(update.getMessage().getChatId(), e.getMessage());
            } catch (TelegramApiException | RuntimeException | IOException  e) {
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
        sendMessage(update.getMessage().getChatId(), "Ошибка команды. Пришлите /help");
    }

    private MessageType getMessageType(Update update) throws UserException {
        MessageType messageType = null;
        try {
            if (update.getMessage().getPhoto() != null)
                messageType = MessageType.IMAGE;
            else if (update.getMessage().getText() != null)
                messageType = (update.getMessage().getText().matches("^/[\\w]*$")) ?
                        MessageType.COMMAND :
                        MessageType.TEXT;
            if (messageType == null)
                throw new IllegalArgumentException(update.toString());
            return messageType;
        } catch (RuntimeException e) {
            log.error(String.format("Invalid message type: %s", e.getMessage()));
            throw new UserException("Неподдерживаемый тип сообщения");
        }
    }


    private void processImage(Update update) throws TelegramApiException, IOException, UserException {
        logMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId(), true, "$image");

        try {
                List<PhotoSize> photoSizes = update.getMessage().getPhoto();
                String fileUrl = getFileUrl(update.getMessage().getPhoto().get(photoSizes.size() - 1).getFileId());

            String messageStr= update.getMessage().getCaption();
            String cmd=messageStr.substring(0,messageStr.indexOf(' '));
            messageStr=messageStr.substring(messageStr.indexOf(' ')+1);
            if (!cmd.equals("/coder")){
                sendMessage(update.getMessage().getChatId(), "Ошибка команды - не распознана");
                return;
            }

            if ( messageStr.isEmpty()){
                sendMessage(update.getMessage().getChatId(), "Ошибка команды - нет текста для кодирования");
                return;
            }

            String answer=httpClient.sendFileToCoderService(Utils.imgToBase64String(ImageIO.read(new URL(fileUrl)),"jpg"),
                    Utils.getKeyByIdChat(update.getMessage().getChatId()), messageStr);

            sendMessage(update.getMessage().getChatId(), answer);

        } catch (Exception e) {
            sendMessage(update.getMessage().getChatId(), "Ошибка команды");
            return;
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
        register(new CommandCoder(httpClient));
        register(new CommandSendResult(httpClient));
        register(new CommandServiceStatus(httpClient));
        register(new CommandTaskStatus(httpClient));
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
    }
}
