package ru.Shorthand;

import ru.Shorthand.Enums.TaskStatus;

import java.util.Date;

public class Task {

    //auto timestamp
    public Date dateAdd=new Date();
    public Date dateComplete;
    TaskStatus status;
    String image;
    String key;
    String text;

    boolean deleted=false;

    Task(String image, String key, String text)
    {
        this.image=image;
        this.key=key;
        this.text=key;
        status=TaskStatus.INQUEUE;
    }


}
