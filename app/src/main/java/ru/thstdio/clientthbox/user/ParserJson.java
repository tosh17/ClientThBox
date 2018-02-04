package ru.thstdio.clientthbox.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.thstdio.clientthbox.connect.message.Message;
import ru.thstdio.clientthbox.fileutil.PDir;
import ru.thstdio.clientthbox.fileutil.PFile;

/**
 * Created by shcherbakov on 30.01.2018.
 */

public class ParserJson {
    public static String parseRequest(String type, String str) {
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
        return null;
    }

    public static PDir parseFolder(String str) throws JSONException {
        JSONObject json = new JSONObject(str);
        return parseJsonToFolder(json);
    }

    public static PFile parseJsonToFile(JSONObject json) throws JSONException {
        PFile file = new PFile();
        file.id = json.getLong("id");
        file.name = json.getString("name");
        file.size = json.getInt("size");
        file.parent = json.getLong("parent");
        return file;
    }

    public static PDir parseJsonToFolder(JSONObject json) throws JSONException {
        PDir folder = new PDir();
        folder.id = json.getLong("id");
        folder.name = json.getString("name");
        folder.size = json.getInt("size");
        folder.parent = json.getLong("parent");
        JSONArray files = json.getJSONArray("files");
        for (int i = 0; i < files.length(); i++)
            folder.addElement(parseJsonToFile(files.getJSONObject(i)));
        JSONArray folders = json.getJSONArray("dirs");
        for (int i = 0; i < folders.length(); i++)
            folder.addElement(parseJsonToFolder(folders.getJSONObject(i)));
        return folder;
    }
}
