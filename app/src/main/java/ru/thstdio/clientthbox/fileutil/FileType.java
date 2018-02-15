package ru.thstdio.clientthbox.fileutil;

/**
 * Created by shcherbakov on 11.02.2018.
 */

public enum FileType {
    Folder,None, Txt, Audio, Video,Image,Arc;

    public static FileType convertType(String str) {
        switch (str) {
            case "txt":
                return Txt;
            case "jpg":
                return Image;
            case "mp3":
                return Audio;
            case "avi":
                return Video;
            default:
                return None;

        }
    }
}
