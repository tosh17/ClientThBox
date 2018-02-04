package ru.thstdio.clientthbox.bus.event;

/**
 * Created by shcherbakov on 03.02.2018.
 */

public class SignUpFreeLogin {
   public  boolean isLoginFree;

    public SignUpFreeLogin(boolean isLoginFree) {
        this.isLoginFree = isLoginFree;
    }
}
