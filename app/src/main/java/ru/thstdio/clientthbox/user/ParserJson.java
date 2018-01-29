package ru.thstdio.clientthbox.user;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shcherbakov on 30.01.2018.
 */

public class ParserJson {
    public static boolean parseAuthRequest(String str){
        JSONObject obj = null;
        try {
            obj = new JSONObject(str);
            if (obj.getString(Message.Request).equals(Message.TYPE_AUTH)) {
                return obj.getString(Message.Data).equals(Message.STATUS_OK);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //todo exeption
        return  false;
    }
}
