package ru.shothandTelegrambot.bot;

public class KeySession {
    public long idChat;
    public String key;
    public KeySession(long idChat, String key)
    {
        this.idChat=idChat;
        this.key=key;
    }
}
