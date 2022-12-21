package ru.shothandTelegrambot.httpClient;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.server.model.internal.SseTypeResolver;
import org.json.JSONObject;
import ru.shothandTelegrambot.bot.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class HttpClient {
    String restServiceUrl = "http://localhost:8080/ShorthandRestService/";
    org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();

    public String getStatusService()
    {
        GetMethod getMethod = new GetMethod(restServiceUrl+"?cmd=status");
        Header mtHeader = new Header();
        mtHeader.setName("content-type");
        mtHeader.setValue("application/json");
        getMethod.addRequestHeader(mtHeader);
        String output=null;
        try {
            httpClient.executeMethod(getMethod);
            output = getMethod.getResponseBodyAsString();
        }
        catch (Exception e)
        {

            return "Ошибка httpClient: "+e.toString();
        }
        if (output!=null){
            JSONObject jsonObj = new JSONObject(output);
            return jsonObj.getString("coderState");
        }
        return "Ошибка сервиса";
    }


   public String getTaskStateService(String numTask) {
       GetMethod getMethod = new GetMethod(restServiceUrl+"?cmd=taskstate&numTask="+numTask);
       Header mtHeader = new Header();
       mtHeader.setName("content-type");
       mtHeader.setValue("application/json");
       getMethod.addRequestHeader(mtHeader);
       String output=null;
       try {
           httpClient.executeMethod(getMethod);
           output = getMethod.getResponseBodyAsString();
       }
       catch (Exception e)
       {

           return "Ошибка httpClient: "+e.toString();
       }
       if (output!=null){
           JSONObject jsonObj = new JSONObject(output);
           return jsonObj.getString("coderTaskState");
       }
       return "Ошибка сервиса";
   }

    public String sendFileToCoderService(String imgBase64, String key, String text) throws MalformedURLException {

        URL url = new URL(restServiceUrl+"?cmd=coder&image="
                                            +imgBase64+"&key="+key+"&text="+text);
//
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        con.setRequestProperty("Content-Type", "application/json");
//        con.setConnectTimeout(5000);
//        con.setReadTimeout(5000);
//
//        String result=null;
//        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
//            String inputLine;
//            final StringBuilder content = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) {
//                content.append(inputLine);
//            }
//            result=content.toString();
//            con.disconnect();
//        } catch (final Exception ex) {
//            ex.printStackTrace();
//            return "Ошибка чтения ответа";
//        }

        GetMethod getMethod = new GetMethod(url.toString());

        Header mtHeader = new Header();
        mtHeader.setName("content-type");
        mtHeader.setValue("application/json");
        getMethod.addRequestHeader(mtHeader);
        String output=null;
        try {
            httpClient.executeMethod(getMethod);
            output = getMethod.getResponseBodyAsString();
        }
        catch (Exception e) {
            return "Ошибка httpClient: "+e.toString();}

        if (output!=null){
            JSONObject jsonObj = new JSONObject(output);
            return "Номер задачи: "+jsonObj.getString("numTask")+". Среднее время ожидания в очереди:"+
                    jsonObj.getString("avgQueueCoderTime")+"мс";
        }
        return "Ошибка сервиса";
    }

    public BufferedImage getFileFromCoderService(String numTask)  {
        String output=null;
        try {
        URL url = new URL(restServiceUrl+"?cmd=getResult&numTask="+numTask);

        GetMethod getMethod = new GetMethod(url.toString());

        Header mtHeader = new Header();
        mtHeader.setName("content-type");
        mtHeader.setValue("application/json");
        getMethod.addRequestHeader(mtHeader);


            httpClient.executeMethod(getMethod);
            output = getMethod.getResponseBodyAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (output!=null){
            JSONObject jsonObj = new JSONObject(output);
            return Utils.base64StringToImg(jsonObj.getString("image"));
        }
        return null;
    }
}
