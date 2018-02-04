package ru.thstdio.clientthbox.bus.event;

/**
 * Created by shcherbakov on 03.02.2018.
 */

public class FolderLoadEvent {
    public String folderStr;

    public FolderLoadEvent(String folderStr) {
        this.folderStr = folderStr;
    }

}
