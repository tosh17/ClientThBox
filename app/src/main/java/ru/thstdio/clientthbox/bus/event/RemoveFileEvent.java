package ru.thstdio.clientthbox.bus.event;

/**
 * Created by shcherbakov on 28.02.2018.
 */

public class RemoveFileEvent {
    boolean ok=true;

    public RemoveFileEvent(boolean ok) {
        this.ok = ok;
    }
}
