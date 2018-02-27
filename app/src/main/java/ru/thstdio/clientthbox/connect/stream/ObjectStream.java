package ru.thstdio.clientthbox.connect.stream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObjectStream implements Stream {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

   // private static final String SERVER_ADDR = "192.168.0.106";
   private static final String SERVER_ADDR = "10.127.127.1";
    private static final int SERVER_PORT = 8189;

    public ObjectStream()  {

    }

    @Override
    public void sendString(String str) throws IOException {
        out.writeObject(str);
        out.flush();
    }

    @Override
    public String readString() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    @Override
    public void sendObject(Object object) throws IOException {
        out.writeObject(object);
        out.flush();
    }


    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    @Override
    public void close() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
    }

    @Override
    public boolean connect() {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean isConnected() {
        if(socket==null) return false;
        return socket.isConnected();
    }


}
