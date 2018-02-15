package ru.thstdio.clientthbox.connect;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import ru.thstdio.clientthbox.bus.BusProvider;
import ru.thstdio.clientthbox.bus.event.ConnectEvent;
import ru.thstdio.clientthbox.bus.event.FolderLoadEvent;
import ru.thstdio.clientthbox.bus.event.LoginEvent;
import ru.thstdio.clientthbox.bus.event.SignUp;
import ru.thstdio.clientthbox.bus.event.SignUpFreeLogin;
import ru.thstdio.clientthbox.connect.message.Message;
import ru.thstdio.clientthbox.connect.message.MessageFile;
import ru.thstdio.clientthbox.connect.message.MessageStatus;
import ru.thstdio.clientthbox.connect.message.MessageType;
import ru.thstdio.clientthbox.connect.stream.ObjectStream;
import ru.thstdio.clientthbox.connect.stream.Stream;
import ru.thstdio.clientthbox.fileutil.PDir;
import ru.thstdio.clientthbox.user.ParserJson;
import ru.thstdio.clientthbox.user.User;


public class ConnectThBox extends Service {

    public static final String KEY_COMMAND = "KEY_COMMAND";
    public static final int COMMAND_NON = 0;
    public static final int COMMAND_CONNECT = 1;
    public static final int COMMAND_LOGIN = 2;
    public static final int COMMAND_SIGN_UP = 4;
    public static final int COMMAND_CHECK_USER = 3;
    public static final int COMMAND_GET_FOLDER = 5;
    public static final int COMMAND_DOWNLOAD_FILE = 6;
    public static final int COMMAND_UPLOAD_FILE= 7;
    public static String KEY_USER_NAME = "KEY_USER_NAME";
    public static String KEY_USER_PASS = "KEY_USER_PASS";
    public static String KEY_FOLDER_ID = "KEY_FOLDER_ID";
    public static String KEY_FILE_ID = "KEY_FOLDER_ID";
    public static String KEY_FILE_NAME = "KEY_FILE_NAME";
    public static String KEY_FILE_URI="KEY_FILE_URI";


    private static final String SERVER_ADDR = "192.168.0.106";
    private static final int SERVER_PORT = 8189;
    private Socket sock;
    private Stream stream;
    private PDir localFolder;



    public ConnectThBox() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name, pass;
        long id;
        File file;
        int command = intent.getIntExtra(KEY_COMMAND, COMMAND_NON);
        switch (command) {
            case COMMAND_CONNECT:
                connectServer();
                break;
            case COMMAND_LOGIN:
                name = intent.getStringExtra(KEY_USER_NAME);
                pass = intent.getStringExtra(KEY_USER_PASS);
                login(name, pass);
                break;
            case COMMAND_SIGN_UP:
                name = intent.getStringExtra(KEY_USER_NAME);
                pass = intent.getStringExtra(KEY_USER_PASS);
                signUp(name, pass);
                break;
            case COMMAND_CHECK_USER:
                name = intent.getStringExtra(KEY_USER_NAME);
                checkUser(name);
                break;
            case COMMAND_GET_FOLDER:
                id = intent.getLongExtra(KEY_FOLDER_ID, 0);
                getFolder(id);
                break;
            case COMMAND_DOWNLOAD_FILE:
                id = intent.getLongExtra(KEY_FILE_ID, 0);
                name = intent.getStringExtra(KEY_FILE_NAME);
                loadFile(id, name);
                break;
            case COMMAND_UPLOAD_FILE:
                file = (File) intent.getSerializableExtra(KEY_FILE_URI);
                upLoadFile(file,0);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void upLoadFile(final File file, final int idFolder) {
        ioMessage(Message.createMessage(MessageType.REQUEST_FILE_UPLOAD, MessageFile.createNewFile(file,idFolder)), new StringWainter() {
            @Override
            public void getString(String str) {
                //todo error
                Log.d("Test",str);
                if (str == null) return;
                String status=MessageStatus.OK;
                if( status.equals(MessageStatus.OK)){
                    ioMessageUpLoad(file, idFolder, new ObjectWainter() {
                        @Override
                        public void getObject(Object object) {

                        }
                    });
                }
                else{}


            }
        });
    }

    private void loadFile(long id, String nameFile) {
        ioMessageLoad(Message.createMessage(MessageType.REQUEST_FILE_DOWNLOAD, String.valueOf(id)), nameFile, new ObjectWainter() {
            @Override
            public void getObject(Object object) {

            }
        });
    }

    private void getFolder(long idFolder) {
        ioMessage(Message.createMessage(MessageType.REQUEST_ROOT_FOLDER, String.valueOf(idFolder)), new StringWainter() {
            @Override
            public void getString(String str) {
                //todo error
                if (str == null) return;
                BusProvider.getInstance().post(
                        new FolderLoadEvent(
                                ParserJson.parseRequest(MessageType.REQUEST_ROOT_FOLDER, str)));
            }
        });
    }

    private void checkUser(String name) {
        ioMessage(Message.createMessage(MessageType.SIGN_UP_FREE_USER, name), new StringWainter() {
            @Override
            public void getString(String str) {
                BusProvider.getInstance().post(new SignUpFreeLogin(
                        ParserJson.parseRequest(MessageType.SIGN_UP_FREE_USER, str).equals(MessageStatus.OK)));
            }
        });
    }

    private void signUp(String name, String pass) {
        User user = new User(name, pass);
        ioMessage(Message.createMessage(MessageType.SIGN_UP, user.getJson()), new StringWainter() {
            @Override
            public void getString(String str) {
                BusProvider.getInstance().post(new SignUp(
                        ParserJson.parseRequest(MessageType.SIGN_UP, str)));
            }
        });
    }

    private void connectServer() {
        final AsyncTask<Void, Void, Boolean> ex = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                if (sock == null) return connect();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                BusProvider.getInstance().post(new ConnectEvent(aBoolean));

                super.onPostExecute(aBoolean);
            }
        };
        ex.execute();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ex.getStatus() != AsyncTask.Status.FINISHED) {
                    ex.cancel(true);
                    connectServer();
                    Log.d("Connect", "Reconnect " + ex.getStatus());
                }
            }
        }, 30000);
    }

    private boolean connect() {
        try {
            sock = new Socket(SERVER_ADDR, SERVER_PORT);
            stream = new ObjectStream(sock);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void login(String name, String pass) {
        User user = new User(name, pass);
        ioMessage(Message.createMessage(MessageType.AUTH, user.getJson()), new StringWainter() {
            @Override
            public void getString(String str) {
                BusProvider.getInstance().post(new LoginEvent(
                        ParserJson.parseRequest(MessageType.AUTH, str).equals(MessageStatus.OK)));
            }
        });

    }

    private void ioMessage(final String str, final StringWainter listern) {

        AsyncTask<String, String, String> ex = new AsyncTask<String, String, String>() {

            String mess;

            @Override
            protected String doInBackground(String... strs) {
                if (sock == null) connect();
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

    private void ioMessageLoad(final String str, final String nameFile, final ObjectWainter listern) {

        AsyncTask<String, String, Object> ex = new AsyncTask<String, String, Object>() {

            String mess;

            @Override
            protected Object doInBackground(String... strs) {
                if (sock == null) connect();
                long fileSize = 0;

                try {
                    stream.sendString(strs[0]);
                    Object msg = null;
                    msg = stream.readObject();
                    try (FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + nameFile)) {
                        // перевод строки в байты

                        byte[] buffer = (byte[]) msg;
                        fos.write(buffer, 0, buffer.length);

                    } catch (IOException ex) {
                        Log.d("INPUT", "Error write file");
                    }
                    return msg;
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
    private void ioMessageUpLoad(final File file,long idFolder, final ObjectWainter listern) {

        AsyncTask<String, String, Object> ex = new AsyncTask<String, String, Object>() {

            String mess;

            @Override
            protected Object doInBackground(String... strs) {
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
