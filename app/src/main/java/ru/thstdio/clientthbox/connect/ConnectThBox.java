package ru.thstdio.clientthbox.connect;


import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
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
import ru.thstdio.clientthbox.connect.stream.MessageIO;
import ru.thstdio.clientthbox.connect.stream.ObjectStream;
import ru.thstdio.clientthbox.connect.stream.Stream;
import ru.thstdio.clientthbox.fileutil.FileType;
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
    public static final int COMMAND_UPLOAD_FILE = 7;
    public static final int COMMAND_CREATE_FOLDER = 8;
    public static final int COMMAND_DELETE_FILE = 9;
    public static final int COMMAND_DELETE_FOLDER = 9;
    public static String KEY_USER_NAME = "KEY_USER_NAME";
    public static String KEY_USER_PASS = "KEY_USER_PASS";
    public static String KEY_FOLDER_ID = "KEY_FOLDER_ID";
    public static String KEY_FILE_ID = "KEY_FOLDER_ID";
    public static String KEY_FILE_NAME = "KEY_FILE_NAME";
    public static String KEY_FILE_URI = "KEY_FILE_URI";
    public static String KEY_STR = "KEY_STR";
    private Stream stream;
    private PDir localFolder;
    private MessageIO messHelper;


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
                id = intent.getLongExtra(KEY_FOLDER_ID, 0);
                upLoadFile(file, id);
                break;
            case COMMAND_CREATE_FOLDER:
                name = intent.getStringExtra(KEY_STR);
                id = intent.getLongExtra(KEY_FOLDER_ID, 0);
                createFolder(name, id);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createFolder(String name, final long idFolderParents) {
        messHelper.ioMessage(Message.createMessage(MessageType.REQUEST_FOLDER_CREATE, MessageFile.createNewFolder(name, idFolderParents)), new StringWainter() {
            @Override
            public void getString(String str) {
                if (str == null) return;
                String status=ParserJson.parseRequest(MessageType.REQUEST_FOLDER_CREATE,str);
                if (status.equals(MessageStatus.OK)) {
                    getFolder(idFolderParents);
                }
            }
        });
    }

    private void upLoadFile(final File file, final long idFolder) {
        messHelper.ioMessage(Message.createMessage(MessageType.REQUEST_FILE_UPLOAD, MessageFile.createNewFile(file, idFolder)), new StringWainter() {
            @Override
            public void getString(String str) {
                //todo error
                if (str == null) return;
                String status=ParserJson.parseRequest(MessageType.REQUEST_FILE_UPLOAD,str);
                if (status.equals(MessageStatus.OK)) {
                    messHelper.ioMessageUpLoad(file, idFolder, new ObjectWainter() {
                        @Override
                        public void getObject(Object object) {

                        }
                    });
                } else {
                }


            }
        });
    }

    private void loadFile(long id, String nameFile) {
        messHelper.ioMessageLoad(Message.createMessage(MessageType.REQUEST_FILE_DOWNLOAD, String.valueOf(id)), nameFile, new ObjectWainter() {
            @Override
            public void getObject(Object object) {
                Intent i=FileType.convertToIntent((String)object);
                PackageManager packageManager = getPackageManager(); if (i.resolveActivity(packageManager) != null)
                    startActivity(FileType.convertToIntent((String)object));
            }
        });
    }

    private void getFolder(long idFolder) {
        messHelper.ioMessage(Message.createMessage(MessageType.REQUEST_ROOT_FOLDER, String.valueOf(idFolder)), new StringWainter() {
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
        messHelper.ioMessage(Message.createMessage(MessageType.SIGN_UP_FREE_USER, name), new StringWainter() {
            @Override
            public void getString(String str) {
                BusProvider.getInstance().post(new SignUpFreeLogin(
                        ParserJson.parseRequest(MessageType.SIGN_UP_FREE_USER, str).equals(MessageStatus.OK)));
            }
        });
    }

    private void signUp(String name, String pass) {
        User user = new User(name, pass);
        messHelper.ioMessage(Message.createMessage(MessageType.SIGN_UP, user.getJson()), new StringWainter() {
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
                if (stream == null) {
                    stream = new ObjectStream();
                    messHelper = new MessageIO(stream);
                    return stream.connect();
                } else return stream.isConnected();

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

    private void login(String name, String pass) {
        User user = new User(name, pass);
         messHelper.ioMessage(Message.createMessage(MessageType.AUTH, user.getJson()), new StringWainter() {
            @Override
            public void getString(String str) {
                BusProvider.getInstance().post(new LoginEvent(
                        ParserJson.parseRequest(MessageType.AUTH, str).equals(MessageStatus.OK)));
            }
        });

    }


}
