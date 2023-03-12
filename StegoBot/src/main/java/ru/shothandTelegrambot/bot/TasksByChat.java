package ru.shothandTelegrambot.bot;

public class TasksByChat {
    public long idChat;
    public int idMessage;
    public long numTask;
    public String state;

    public boolean decrypt;

    public int numForUser = -1;

    public TasksByChat(long idChat, int idMessage, long numTask, boolean decrypt, String state) {
        this.idChat = idChat;
        this.idMessage = idMessage;
        this.numTask = numTask;
        this.decrypt = decrypt;
        this.state = state;
    }
}