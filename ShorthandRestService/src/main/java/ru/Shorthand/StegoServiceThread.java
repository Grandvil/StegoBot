package ru.Shorthand;

import ru.Shorthand.Enums.EnumTaskStatus;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.randomlsb.StegoCore;

public class StegoServiceThread {


    static int lifeCycleTaskSec = 180;

    static int threadCount = 4;
    static int threadSleep = 100; //// не быстрее 100 мс поток крутится
    ArrayList<Thread> threadCoder = new ArrayList<>();
    public boolean fRunServiceThread = true;


    StegoServiceThread(CopyOnWriteArrayList<StegoServiceTask> tasks) {
        CoderStart(tasks);
    }

    void CoderStart(CopyOnWriteArrayList<StegoServiceTask> tasks) {
        for (int i = 0; i < threadCount; i++)
            threadCoder.add(new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println("CoderThread #" + Thread.currentThread().getId() + " start");
                    long timeCleanup = (long) 1000 * lifeCycleTaskSec;
                    StegoCore stegoCore = new StegoCore();
                    while (fRunServiceThread) {
                        int indexTask = -1;
                        try {
                            for (StegoServiceTask task : tasks) {

                                indexTask = tasks.indexOf(task);
                                if (task.fGet) continue;
                                task.fGet = true;

                                if (task.dateComplete == null && !task.deleted) {
                                    task.status = EnumTaskStatus.ACTIVE;

                                    Thread.sleep(30000);
                                    if (task.decrypt)
                                        task.text = stegoCore.Decrypt(StegoServiceUtils.base64StringToImg(task.image), task.key);
                                    else {

                                        BufferedImage bi = StegoServiceUtils.base64StringToImg(task.image);

                                        bi = stegoCore.Encrypt(bi, task.key, task.text);

                                        task.image = StegoServiceUtils.imgToBase64String(
                                                bi,
                                                "png");
                                    }
                                    task.dateComplete = new Date();
                                    task.status = EnumTaskStatus.READY;
                                    System.out.println("CoderThread task ok");
                                }


                                if (task.status != EnumTaskStatus.READY || task.deleted) continue;


                                Date now = new Date();
                                long time = now.getTime() - task.dateComplete.getTime();

                                if (time >= timeCleanup) {

                                    task.deleted = true;
                                    task.text = null;
                                    task.image = null;
                                    task.key = null;
                                }
                                task.fGet = false;

                            }
                        } catch (Exception e) {
                            System.out.println("threadCoder exception: " + e.toString());
                            e.printStackTrace();
                            if (indexTask != -1)
                                tasks.remove(indexTask);
                        }


                        try {
                            Thread.sleep(threadSleep);
                        } catch (InterruptedException e) {
                            System.out.println("threadCoder sleep exception: " + e.toString());
                            e.printStackTrace();
                        }
                    }
                }
            }));

        for (Thread tc : threadCoder)
            tc.start();
    }
}