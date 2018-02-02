package ru.thstdio.clientthbox;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.thstdio.clientthbox.bus.BusProvider;
import ru.thstdio.clientthbox.bus.event.ConnectEvent;
import ru.thstdio.clientthbox.bus.event.LoginEvent;
import ru.thstdio.clientthbox.bus.event.SignUp;
import ru.thstdio.clientthbox.connect.ConnectThBox;

public class Login extends AppCompatActivity {


    @BindView(R.id.loginCardView)
    View loginCardView;
    @BindView(R.id.loginUserName)
    EditText loginUserName;
    @BindView(R.id.loginPassword)
    EditText loginPassword;
    @BindView(R.id.loginPasswordSecond)
    EditText loginPasswordSecond;

    @BindView(R.id.loginButtonOk)
    Button loginButtonOk;
    @BindView(R.id.loginSignUp)
    TextView loginSignUp;

    @BindView(R.id.disconnect)
    View disconnect;
    @BindView(R.id.loginForm)
    View loginForm;
    @BindView(R.id.loginFormPasswordSecond)
    View loginFormPasswordSecond;

    Animation shakeanimation;
    boolean isSignUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
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
        Intent intent = new Intent(this, ConnectThBox.class);
        intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_CONNECT);
        startService(intent);

    }

    @OnClick(R.id.loginButtonOk)
    public void clickLogin() {
        Intent intent = new Intent(this, ConnectThBox.class);
        if (!isSignUp) {
            intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_LOGIN);
        }
        else{
            intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_SIGN_UP);
        }
        intent.putExtra(ConnectThBox.KEY_USER_NAME, loginUserName.getEditableText().toString());
        intent.putExtra(ConnectThBox.KEY_USER_PASS, loginPassword.getEditableText().toString());
        startService(intent);

    }

    @OnClick(R.id.loginSignUp)
    public void clickSignUp() {
        isSignUp = !isSignUp;
        if (isSignUp) {
            loginFormPasswordSecond.setVisibility(View.VISIBLE);
        //    loginButtonOk.setEnabled(false);
        }
    }

    @Subscribe
    public void onConnect(@NonNull ConnectEvent event) {
        disconnect.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);
        loginButtonOk.setEnabled(true);
    }

    @Subscribe
    public void onLogin(@NonNull LoginEvent event) {
        if (event.isLogin)
            Toast.makeText(getApplication(), "Login", Toast.LENGTH_LONG).show();
        else {
            loginCardView.startAnimation(shakeanimation);
            loginPassword.setText("");
        }
    }
    @Subscribe
    public void onSignUp(@NonNull SignUp event) {
        Toast.makeText(getApplication(), "User Create", Toast.LENGTH_LONG).show();
    }
}
