package ru.thstdio.clientthbox.connect.stream;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.thstdio.clientthbox.connect.ObjectWainter;
import ru.thstdio.clientthbox.connect.StringWainter;

/**
 * Created by shcherbakov on 20.02.2018.
 */

public class MessageIO {
    Stream stream;

    public MessageIO(Stream stream) {
        this.stream = stream;
    }

    public void ioMessage(final String str, final StringWainter listern) {

        AsyncTask<String, String, String> ex = new AsyncTask<String, String, String>() {

            String mess;

            @Override
            protected String doInBackground(String... strs) {
                if (!stream.isConnected()) stream.connect();
                try {
                    stream.sendString(strs[0]);
                    String msg = stream.readString();
                    Log.d("INPUT", msg);
                    return msg;
                } catch (IOException e) {
                    return null;
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                listern.getString(s);
            }

        };
        ex.execute(str);
    }
    public void ioMessageLoad(final String str, final String nameFile, final ObjectWainter listern) {

        AsyncTask<String, String, Object> ex = new AsyncTask<String, String, Object>() {

            String mess;

            @Override
            protected Object doInBackground(String... strs) {

                long fileSize = 0;
                if (!stream.isConnected()) stream.connect();
                try {
                    stream.sendString(strs[0]);
                    Object msg = null;
                    msg = stream.readObject();
                    //Fixme check permission
                    String downPath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath()+"/" + nameFile;
                    try (FileOutputStream fos = new FileOutputStream(downPath )) {
                        // перевод строки в байты

                        byte[] buffer = (byte[]) msg;
                        fos.write(buffer, 0, buffer.length);

                    } catch (IOException ex) {
                        Log.d("INPUT", "Error write file");
                    }


                    return downPath;
                } catch (IOException e) {
                    return null;
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object s) {
                super.onPostExecute(s);
                listern.getObject(s);
            }

        };
        ex.execute(str);
    }
    public void ioMessageUpLoad(final File file, long idFolder, final ObjectWainter listern) {

        AsyncTask<String, String, Object> ex = new AsyncTask<String, String, Object>() {

            String mess;

            @Override
            protected Object doInBackground(String... strs) {
                if (!stream.isConnected()) stream.connect();
                byte[] buffer = null;
                try (FileInputStream fin = new FileInputStream(file.getCanonicalPath())) {
                    //       System.out.println("Размер файла: " + fin.available() + " байт(а)");
                    buffer = new byte[fin.available()];
                    fin.read(buffer, 0, fin.available());
                    stream.sendObject(buffer);
                } catch (IOException ex) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object s) {
                super.onPostExecute(s);
                listern.getObject(s);
            }

        };
        ex.execute();
    }
}
