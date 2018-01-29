package ru.thstdio.clientthbox;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.thstdio.clientthbox.user.Message;
import ru.thstdio.clientthbox.user.ParserJson;
import ru.thstdio.clientthbox.user.User;

public class Login extends AppCompatActivity {

    private static final String SERVER_ADDR = "192.168.0.111";
    private static final int SERVER_PORT = 8189;
    private boolean status;
    private Socket sock;
    private DataInputStream in;
    private DataOutputStream out;

    @BindView(R.id.loginCardView)
    View loginCardView;
    @BindView(R.id.loginUserName)
    EditText loginUserName;
    @BindView(R.id.loginPassword)
    EditText loginPassword;

    Animation shakeanimation ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();
        ButterKnife.bind(this);
        shakeanimation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @OnClick(R.id.loginButtonOk)
    public void clicLogin() {
        login();
    }

    private void login() {
        AsyncTask<Void, Void, String> ex = new AsyncTask<Void, Void, String>() {
            User user;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                user = new User();
                user.login = loginUserName.getEditableText().toString();
                user.password = loginPassword.getEditableText().toString();

            }

            @Override
            protected String doInBackground(Void... voids) {
                if (sock == null) connect();
                return sendAuth();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (ParserJson.parseAuthRequest(s))
                    Toast.makeText(getApplication(), "Login", Toast.LENGTH_LONG).show();
                else {
                    loginCardView.startAnimation(shakeanimation);
                    loginPassword.setText("");
                }
            }

            private void connect() {
                status = false;
                try {
                    sock = new Socket(SERVER_ADDR, SERVER_PORT);
                    in = new DataInputStream(sock.getInputStream());
                    out = new DataOutputStream(sock.getOutputStream());
                    status = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private String sendAuth() {
                try {
                    out.writeUTF(Message.createMessage(Message.TYPE_AUTH, user.getJson()));
                    return in.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "error";
            }
        };
        ex.execute();
    }
}
