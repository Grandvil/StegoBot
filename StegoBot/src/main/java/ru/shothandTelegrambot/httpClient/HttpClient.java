package ru.shothandTelegrambot.httpClient;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import ru.shothandTelegrambot.bot.Utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

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

    public String sendFileToCoderService(String imgBase64, String key, String text, boolean crypt) throws MalformedURLException, UnsupportedEncodingException {

        URL url;
        if (crypt)
         url = new URL(restServiceUrl+"?cmd=coder");
        else
         url = new URL(restServiceUrl+"?cmd=decoder");

        JSONObject postJSON = new JSONObject();
        postJSON.put("image", imgBase64);
        postJSON.put("key", key);
        if (crypt)
            postJSON.put("text", text);

        PostMethod postMethod = new PostMethod(url.toString());

        Header mtHeader = new Header();
        mtHeader.setName("content-type");
        mtHeader.setValue("application/json");
        postMethod.addRequestHeader(mtHeader);
        StringRequestEntity requestEntity = new StringRequestEntity(
                postJSON.toString(),
                "application/json",
                "UTF-8");

        postMethod.setRequestEntity(requestEntity);

        String output=null;
        try {
            httpClient.executeMethod(postMethod);
            output = postMethod.getResponseBodyAsString();
        }
        catch (Exception e) {
            return "Ошибка httpClient: "+e.toString();}

        if (output!=null){
            JSONObject jsonObj = new JSONObject(output);
            return "Номер задачи: "+jsonObj.getString("numTask")+". Среднее время ожидания в очереди: "+
                    jsonObj.getString("avgQueueCoderTime")+"мс";
        }
        return "Ошибка сервиса";
    }

    public String getDecodedMessage(String numTask){
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
            return jsonObj.getString("image");
        }
        return null;
    }
    public JSONObject getFileFromCoderService(String numTask)  { //BufferedImage
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
            return new JSONObject(output);//Utils.base64StringToImg(jsonObj.getString("image"));
        }
        return null;
    }
}
