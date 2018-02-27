package ru.thstdio.clientthbox.fileutil;

import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

/**
 * Created by shcherbakov on 11.02.2018.
 */

public enum FileType {
    Folder, None, Txt, Audio, Video, Image, Arc;

    public static FileType convertType(String str) {
        switch (str) {
            case "txt":
                return Txt;
            case "jpg":
                return Image;
            case "png":
                return Image;
            case "gif":
                return Image;
            case "mp3":
                return Audio;
            case "avi":
                return Video;
            case "mp4":
                return Video;
            default:
                return None;

        }
    }

    public static Intent convertToIntent(String patch) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(getTypeStr(patch));
        newIntent.setDataAndType(Uri.parse(patch), mimeType);
        return newIntent;
    }

    public static FileType getType(String name) {
        String[] temp = name.split("\\.");
        if (temp.length > 1) return (FileType.convertType(temp[temp.length - 1]));
        return FileType.None;
    }

    public static String getTypeStr(String name) {
        String[] temp = name.split("\\.");
        if (temp.length > 1)
            return temp[temp.length - 1];
        return "";
    }
}
