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
    public static String SIZE="size";

    public static String createNewFile(File file, long id){
        JSONObject json = new JSONObject();
        try {
            json.put(FILE, file.getName());
            json.put(FOLDER, id);
            json.put(SIZE, file.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();

    }

    public static String createNewFolder(String name, long idFolderParents) {
        JSONObject json = new JSONObject();
        try {
            json.put(FILE, name);
            json.put(FOLDER, idFolderParents);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
