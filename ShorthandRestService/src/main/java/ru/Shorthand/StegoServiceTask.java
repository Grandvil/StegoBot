package ru.Shorthand;

import ru.Shorthand.Enums.EnumTaskStatus;

import java.util.Date;

public class StegoServiceTask {

    //auto timestamp
    public Date dateAdd=new Date();
    public Date dateComplete;
    public EnumTaskStatus status;
    public String image;
    public String key;
    public String text;

    boolean deleted=false;

    public boolean decrypt=false;

    StegoServiceTask(String image, String key, String text, boolean decrypt)
    {
        this.image=image;
        this.key=key;
        this.text=text;
        this.decrypt=decrypt;
        status= EnumTaskStatus.INQUEUE;
    }


}
