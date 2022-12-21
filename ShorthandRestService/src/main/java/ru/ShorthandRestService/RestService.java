package ru.ShorthandRestService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import ru.Shorthand.Core;

public class RestService extends HttpServlet
{
    /**
     *
     */
    private static final long serialVersionUID = 102831973239L;

    String buildBadCommand(String description){
        try {
            JSONObject answer = new JSONObject();
            answer.put("coder","BAD_COMMAND");
            answer.put("description", description);
            return answer.toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            System.out.println("RestService: JSON error: "+e.toString());
            return "Json error";
        }
    }

    @Override
    public void init() throws ServletException {
        // TODO Auto-generated method stub
        super.init();
    }
    public void doGet(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        //res.setContentType("text/html");
        res.setContentType("application/json");
        //get core coder
        Core core;
        ServletContext context = getServletContext();
        core=(Core)context.getAttribute("core");
        if (core == null){
            out.println("core in context not found");
            return;
        }

        String numTaskStr=null;
        int numTask=0;

        switch (req.getParameter("cmd"))
        {
            case "status":
                out.println(core.getServiceStatus());
                break;

            case "taskstate":
                numTaskStr=req.getParameter("numTask");
                if (numTaskStr==null) {
                    out.println(buildBadCommand("Отсутствует номер задачи"));
                    return;
                }

                try {
                    numTask = Integer.parseInt(numTaskStr);
                }
                catch (Exception e){
                    out.println(buildBadCommand("Не возможно преобразовать номер задачи в число"));
                    return;
                }

                out.println(core.getTaskState(numTask));
                break;

            case "coder":
                String image=req.getParameter("image");
                if (image==null) {
                    out.println(buildBadCommand("Отсутствует изображение в запросе"));
                    return;
                }
                String stegoKey=req.getParameter("key");
                if (stegoKey==null) {
                    out.println(buildBadCommand("Отсутствует ключ в запросе"));
                    return;
                }
                String text=req.getParameter("text");
                if (text==null) {
                    out.println(buildBadCommand("Отсутствует текст для кодирования в запросе"));
                    return;
                }
                out.println(core.addNewTask(image,stegoKey,text));
                break;

            case "getResult":
                numTaskStr=req.getParameter("numTask");
                if (numTaskStr==null) {
                    out.println(buildBadCommand("Отсутствует номер задачи"));
                    return;
                }

                try {
                    numTask = Integer.parseInt(numTaskStr);
                }
                catch (Exception e){
                    out.println(buildBadCommand("Не возможно преобразовать номер задачи в число"));
                    return;
                }
                out.println(core.getResultTask(numTask));
                break;

            default:
                out.println(buildBadCommand("Не распознанная команда"));
                System.out.println("RestService bad cmd request: "+req.getParameter("cmd"));
                break;
        }
    }
}
