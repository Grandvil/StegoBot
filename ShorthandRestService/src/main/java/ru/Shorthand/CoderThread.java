package ru.Shorthand;

import ru.Shorthand.Enums.TaskStatus;

import java.util.ArrayList;
import java.util.Date;

public class CoderThread {
    static int countCoderThreads=1;
    Thread threadCoder;
    public boolean Run=true;

    CoderThread(ArrayList<Task> tasks)
    {
        CoderStart(tasks);
    }
    void CoderStart(ArrayList<Task> tasks)
    {
        threadCoder = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    while (Run) {
                        for (Task task : tasks) {
                            if (task.dateComplete == null) {
                                task.status=TaskStatus.ACTIVE;
                                //System.out.println("CoderThread active");
                                Thread.sleep(30000);
                                task.dateComplete = new Date();
                                task.status=TaskStatus.READY;
                                //System.out.println("CoderThread ready");
                            }
                        }
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
