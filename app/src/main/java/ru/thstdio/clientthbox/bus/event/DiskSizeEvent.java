package ru.thstdio.clientthbox.bus.event;

/**
 * Created by shcherbakov on 12.03.2018.
 */

public class DiskSizeEvent {
    public long size;

    public DiskSizeEvent(long size) {
        this.size = size;
    }
}
