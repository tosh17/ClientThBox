package ru.thstdio.clientthbox.connect.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shcherbakov on 30.01.2018.
 */

public class Message {
    public static final String Request = "request";
    public static final String Data = "data";

    static public String createMessage(String type, String date) {
        JSONObject json = new JSONObject();
        try {
            json.put(Request, type);
            json.put(Data, date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    static public String createMessage(String type, JSONObject date) {
        JSONObject json = new JSONObject();
        try {
            json.put(Request, type);
            json.put(Data, date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

}
