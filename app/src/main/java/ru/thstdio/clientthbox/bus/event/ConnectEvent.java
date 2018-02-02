package ru.thstdio.clientthbox.bus.event;

/**
 * Created by shcherbakov on 02.02.2018.
 */

public class ConnectEvent {
public boolean isConnect;

    public ConnectEvent(boolean isConnect) {
        this.isConnect = isConnect;
    }
}

