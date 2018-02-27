package ru.thstdio.clientthbox.connect.stream;

import java.io.IOException;

public interface Stream {
   public void sendString(String str) throws IOException;
   public String readString() throws IOException, ClassNotFoundException;
   public void sendObject(Object object) throws IOException;
   public Object readObject() throws IOException, ClassNotFoundException;
   public void close() throws IOException;
   public boolean connect();
   public boolean isConnected();

}
