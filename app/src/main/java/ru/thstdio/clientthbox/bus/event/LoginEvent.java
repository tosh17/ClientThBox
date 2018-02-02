package ru.thstdio.clientthbox.bus.event;

/**
 * Created by shcherbakov on 02.02.2018.
 */

public class LoginEvent {
    public boolean isLogin;

    public LoginEvent(boolean isLogin) {
        this.isLogin = isLogin;
    }
}
