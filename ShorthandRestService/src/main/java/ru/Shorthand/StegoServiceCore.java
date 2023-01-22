package ru.Shorthand;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import ru.Shorthand.Enums.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//core class for coder
public class StegoServiceCore {
    //3 minutes life of result coded
    static int lifeCycleTaskSec=180;
    static int sleepThread=200; //200ms sleep
    Thread threadCleanup;
    StegoServiceCoderThread coderThread;
    EnumServiceStatus status;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static ArrayList<StegoServiceTask> tasks = new ArrayList<>();


    boolean threadCleanupRun=true;

    public StegoServiceCore()
    {
        TaskCleanupThread();
        coderThread=new StegoServiceCoderThread(tasks);
        System.out.println("Core: Coder thread started");
        status= EnumServiceStatus.ACTIVE;
    }

    public void Stop()
    {
        coderThread.Run=false;
        threadCleanupRun=false;
    }

    void TaskCleanupThread()
    {
        threadCleanup = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    long timeCleanup=(long)1000*lifeCycleTaskSec;
                    while (threadCleanupRun) {

                        for (StegoServiceTask task : tasks) {
                            Date now = new Date();
                            if ((task.dateComplete == null) || (task.deleted)) continue;
                            long time=now.getTime() - task.dateComplete.getTime();
                            //System.out.println("TaskCleanupThread time: "+ (now.getTime()-task.dateComplete.getTime()));
                            if (time >= timeCleanup) {
                                //System.out.println("TaskCleanupThread time: "+ time);
                                //iterator.remove();
                                task.deleted = true;
                                task.text = null;
                                task.image = null;
                                task.key = null;
                            }
                        }
                        Thread.sleep(sleepThread);
                    }
                }
                catch (Exception e)
                {
                    System.out.println("TaskCleanupThread exception: "+e.toString());
                    e.printStackTrace();
                }
            }
        });
        threadCleanup.start();
        System.out.println("Core: Cleanup thread started");
    }
    public String getServiceStatus() {
        try {
            JSONObject answer = new JSONObject();
            answer.put("coderState", status.name());
            return answer.toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            System.out.println("Core.getServiceStatus: JSON error: "+e.toString());
            return "Json error";
        }
    }

    public String getTaskState(int num)
    {
        String stateTask;
        try {
            if (!tasks.get(num).deleted)
                stateTask=tasks.get(num).status.name();
            else
                stateTask= EnumTaskStatus.NOTFOUND.name();
        } catch ( IndexOutOfBoundsException e ) {
            stateTask= EnumTaskStatus.NOTFOUND.name();
        }

        try {
            JSONObject answer = new JSONObject();
            answer.put("coderTaskState", stateTask);
            if (!stateTask.equals(EnumTaskStatus.NOTFOUND.name())){
                answer.put("datetimeStart", dateFormat.format(tasks.get(num).dateAdd));
                String date2="null";
                long diffInSec=0;
                if (tasks.get(num).dateComplete!=null) {
                    date2=dateFormat.format(tasks.get(num).dateComplete);
                    diffInSec = Math.abs(tasks.get(num).dateAdd.getTime() -
                            tasks.get(num).dateComplete.getTime()) / 1000;
                }
                answer.put("datetimeEnd", date2);
                answer.put("timeToCode", String.valueOf(diffInSec));
            }
            return answer.toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            System.out.println("Core.getTaskState: JSON error: "+e.toString());
            return "Json error";
        }

    }

    public String getResultTask(int num)
    {
        String img="ok";
        try {
            StegoServiceTask a=tasks.get(num);
            if (a.deleted) img="null";
        } catch ( IndexOutOfBoundsException e ) {
            img="null";
        }
        if (tasks.get(num).status!= EnumTaskStatus.READY)
            img="null";

        try {
            JSONObject answer = new JSONObject();
            if (tasks.get(num).decrypt){
                if (tasks.get(num).status == EnumTaskStatus.READY)
                    answer.put("text", tasks.get(num).text);
                else
                   return  "Task in not ready";
            }
            else {
                if (img.equals("ok"))
                    img = tasks.get(num).image;
                answer.put("image", img);
                answer.put("imageFormat", "png");
            }
            return answer.toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            System.out.println("Core.getResultTask: JSON error: "+e.toString());
            return "Json error";
        }
    }
    public String addNewTask(String image, String key, String text, boolean decrypt)
    {
        StegoServiceTask _task=new StegoServiceTask(image,key,text,decrypt);
        tasks.add(_task);
        try {
            JSONObject answer = new JSONObject();
            answer.put("numTask",String.valueOf(tasks.indexOf(_task)));
            answer.put("avgQueueCoderTime","100");
            return answer.toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            System.out.println("Core.addNewTask: JSON error: "+e.toString());
            return "Json error";
        }
    }
}
