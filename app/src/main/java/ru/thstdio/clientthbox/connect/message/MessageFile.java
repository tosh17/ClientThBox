package ru.thstdio.clientthbox.connect.message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by shcherbakov on 11.02.2018.
 */

public class MessageFile {
    public static String FILE="file";
    public static String FOLDER="folder";

    public static String createNewFile(File file, long id){
        JSONObject json = new JSONObject();
        try {
            json.put(FILE, file.getName());
            json.put(FOLDER, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();

    }
}
