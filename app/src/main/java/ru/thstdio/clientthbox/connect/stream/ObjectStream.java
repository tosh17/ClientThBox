package ru.thstdio.clientthbox.connect.stream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObjectStream implements Stream {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    public ObjectStream(Socket socket) throws IOException {
        this.socket = socket;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
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
}
