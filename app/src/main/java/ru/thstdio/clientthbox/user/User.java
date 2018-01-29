package ru.thstdio.clientthbox.user;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shcherbakov on 30.01.2018.
 */

public class User {
    public String login;
    public String password;

    public JSONObject getJson() {
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("login", login);
            userJson.put("pass", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userJson;
    }
}
