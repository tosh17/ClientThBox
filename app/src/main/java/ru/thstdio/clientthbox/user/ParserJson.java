package ru.thstdio.clientthbox.user;

import org.json.JSONException;
import org.json.JSONObject;

import ru.thstdio.clientthbox.connect.message.Message;

/**
 * Created by shcherbakov on 30.01.2018.
 */

public class ParserJson {
    public static String parseRequest(String type,String str){
        JSONObject obj = null;
        try {
            obj = new JSONObject(str);
            if (obj.getString(Message.Request).equals(type)) {
                return obj.getString(Message.Data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //todo exeption
        return  null;
    }
}
