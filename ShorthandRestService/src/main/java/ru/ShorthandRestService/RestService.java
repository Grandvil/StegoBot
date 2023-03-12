package ru.ShorthandRestService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import ru.Shorthand.StegoServiceCore;

public class RestService extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 102831973239L;

    String buildBadCommand(String description) {
        try {
            JSONObject answer = new JSONObject();
            answer.put("coder", "BAD_COMMAND");
            answer.put("description", description);
            return answer.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("RestService: JSON error: " + e.toString());
            return "Json error";
        }
    }

    @Override
    public void init() throws ServletException {
        // TODO Auto-generated method stub
        super.init();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        res.setContentType("application/json");

        StegoServiceCore core;
        ServletContext context = getServletContext();
        core = (StegoServiceCore) context.getAttribute("core");
        if (core == null) {
            out.println("core in context not found");
            return;
        }

        String numTaskStr = null;
        int numTask = 0;

        switch (req.getParameter("cmd")) {
            case "status":
                out.println(core.getServiceStatus());
                break;

            case "taskstate":

                numTaskStr = req.getParameter("numTask");
                if (numTaskStr == null) {
                    out.println(buildBadCommand("Отсутствует номер задачи"));
                    return;
                }

                try {
                    numTask = Integer.parseInt(numTaskStr);
                } catch (Exception e) {
                    out.println(buildBadCommand("Не возможно преобразовать номер задачи в число"));
                    return;
                }

                out.println(core.getTaskState(numTask));
                break;

            case "decoder":
            case "coder":
                out.println(buildBadCommand("Используйте метод POST json для передачи задачи на кодирование"));
                break;

            case "getResult":

                numTaskStr = req.getParameter("numTask");
                if (numTaskStr == null) {
                    out.println(buildBadCommand("Отсутствует номер задачи"));
                    return;
                }

                try {
                    numTask = Integer.parseInt(numTaskStr);
                } catch (Exception e) {
                    out.println(buildBadCommand("Не возможно преобразовать номер задачи в число"));
                    return;
                }

                out.println(core.getResultTask(numTask));
                break;

            default:
                out.println(buildBadCommand("Не распознанная команда"));
                System.out.println("RestService bad cmd request: " + req.getParameter("cmd"));
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        StegoServiceCore core;
        ServletContext context = getServletContext();
        core = (StegoServiceCore) context.getAttribute("core");
        if (core == null) {
            out.println("core in context not found");
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        JSONObject post;
        String image, stegoKey, text;
        switch (request.getParameter("cmd")) {
            case "coder":

                try {
                    post = new JSONObject(sb.toString());
                    image = post.getString("image");
                    if (image == null) {
                        out.println(buildBadCommand("Отсутствует изображение в запросе"));
                        return;
                    }
                    stegoKey = post.getString("key");
                    if (stegoKey == null) {
                        out.println(buildBadCommand("Отсутствует ключ в запросе"));
                        return;
                    }
                    text = post.getString("text");
                    if (text == null) {
                        out.println(buildBadCommand("Отсутствует текст для кодирования в запросе"));
                        return;
                    }
                } catch (JSONException e) {
                    out.println(buildBadCommand("Не верный JSON. Подробности:" + e.toString()));
                    return;
                }

                out.println(core.addNewTask(image, stegoKey, text, false));
                break;
            case "decoder":
                try {
                    post = new JSONObject(sb.toString());
                    image = post.getString("image");
                    if (image == null) {
                        out.println(buildBadCommand("Отсутствует изображение в запросе"));
                        return;
                    }
                    stegoKey = post.getString("key");
                    if (stegoKey == null) {
                        out.println(buildBadCommand("Отсутствует ключ в запросе"));
                        return;
                    }

                } catch (JSONException e) {
                    out.println(buildBadCommand("Не верный JSON. Подробности:" + e.toString()));
                    return;
                }

                out.println(core.addNewTask(image, stegoKey, null, true));
                break;
            default:
                out.println(buildBadCommand("Не распознанная команда для метода POST"));
                System.out.println("RestService bad cmd request: " + request.getParameter("cmd"));
                break;
        }


    }
}
