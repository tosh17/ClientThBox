package ru.thstdio.clientthbox.connect;

import java.io.IOException;

public interface Stream {
   public void sendString(String str) throws IOException;
   public String readString() throws IOException, ClassNotFoundException;
   public void close() throws IOException;
}
