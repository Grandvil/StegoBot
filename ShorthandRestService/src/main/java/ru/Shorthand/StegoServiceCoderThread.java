package ru.Shorthand;

import ru.Shorthand.Enums.EnumTaskStatus;

import java.util.ArrayList;
import java.util.Date;

import ru.randomlsb.StegoCore;

public class StegoServiceCoderThread {

    static int countCoderThreads=1;
    Thread threadCoder;

    StegoCore stegoCore=new StegoCore();
    public boolean Run=true;

    public boolean outage=true;


    StegoServiceCoderThread(ArrayList<StegoServiceTask> tasks)
    {
        CoderStart(tasks);
    }
    void CoderStart(ArrayList<StegoServiceTask> tasks)
    {
        threadCoder = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    while (Run) {
                        //for (Task task : tasks) {
                        try {
                            for (StegoServiceTask task : tasks) {
                                if (task.dateComplete == null && !task.deleted) {
                                    task.status = EnumTaskStatus.ACTIVE;
                                    //System.out.println("CoderThread active");
                                    //Thread.sleep(30000);
                                    if (task.decrypt)
                                      task.text=stegoCore.Decrypt(StegoServiceUtils.base64StringToImg(task.image),task.key);
                                    else
                                        task.image = StegoServiceUtils.imgToBase64String(
                                                stegoCore.Encrypt(StegoServiceUtils.base64StringToImg(task.image), task.key, task.text),
                                                "png");

                                    task.dateComplete = new Date();
                                    task.status = EnumTaskStatus.READY;
                                }
                                //System.out.println("CoderThread ready");
                            }
                        }
                        catch (Exception ignored){};
                        Thread.sleep(100); //не быстрее 100мс поток крутится
                    }
                }
                catch (Exception e)
                {
                    System.out.println("CoderThread exception: "+e.toString());
                    e.printStackTrace();
                }
            }
        });
        threadCoder.start();
    }
}
