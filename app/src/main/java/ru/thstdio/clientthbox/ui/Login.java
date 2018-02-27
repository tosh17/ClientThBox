package ru.thstdio.clientthbox.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import ru.thstdio.clientthbox.R;
import ru.thstdio.clientthbox.bus.BusProvider;
import ru.thstdio.clientthbox.bus.event.ConnectEvent;
import ru.thstdio.clientthbox.bus.event.LoginEvent;
import ru.thstdio.clientthbox.bus.event.SignUp;
import ru.thstdio.clientthbox.bus.event.SignUpFreeLogin;
import ru.thstdio.clientthbox.connect.ConnectThBox;
import ru.thstdio.clientthbox.connect.message.MessageStatus;
import ru.thstdio.clientthbox.ui.fragment.FolderView;

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

    @BindDrawable(R.drawable.ic_login_ok)
    Drawable icLoginOk;
    @BindDrawable(R.drawable.ic_login_cancel)
    Drawable icLoginCancel;
    @BindView(R.id.loginIcUserName)
    ImageView loginIcUserName;
    @BindView(R.id.loginIcPassword1)
    ImageView loginIcPassword1;
    @BindView(R.id.loginIcPassword2)
    ImageView loginIcPassword2;


    private int MinSizeLogin = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        BusProvider.getInstance().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
         android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();
        ButterKnife.bind(this);
  connect();
        shakeanimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        loginUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (isSignUp && loginUserName.getEditableText().length() > MinSizeLogin) {
                    Intent intent = new Intent(getApplicationContext(), ConnectThBox.class);
                    intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_CHECK_USER);
                    intent.putExtra(ConnectThBox.KEY_USER_NAME, loginUserName.getEditableText().toString());
                    startService(intent);
                }
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
            }
public void connect(){
    Intent intent = new Intent(this, ConnectThBox.class);
    intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_CONNECT);
    startService(intent);
}
    @OnClick(R.id.loginButtonOk)
    public void clickLogin() {
        Intent intent = new Intent(this, ConnectThBox.class);
        if (!isSignUp) {
            intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_LOGIN);
        } else {
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

    @OnTextChanged(R.id.loginPassword)
    public void onTextChangePassword() {
        if (isSignUp) checkPassword();
    }

    @OnTextChanged(R.id.loginPasswordSecond)
    public void onTextChangePassword2() {
        if (isSignUp) checkPassword();
    }

    private void checkPassword() {
        if (loginPassword.getEditableText().toString().equals(loginPasswordSecond.getEditableText().toString())) {
            setIcon(loginIcPassword1, true);
            setIcon(loginIcPassword2, true);
        } else {
            setIcon(loginIcPassword1, false);
            setIcon(loginIcPassword2, false);
        }
    }

    @Subscribe
    public void onConnect(@NonNull ConnectEvent event) {
        disconnect.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);
        loginButtonOk.setEnabled(true);
        SharedPreferences preff = PreferenceManager.getDefaultSharedPreferences(this);
        String user = preff.getString("UserName","");
        String pass =preff.getString("UserPass","");
        loginUserName.setText(user);
        loginPassword.setText(pass);
        if(!user.equals("")){
            clickLogin();
        }
    }

    @Subscribe
    public void onLogin(@NonNull LoginEvent event) {
        if (event.isLogin)
            toMainActivity();
        else {
            loginCardView.startAnimation(shakeanimation);
            loginPassword.setText("");
        }
    }

    @Subscribe
    public void onSignUp(@NonNull SignUp event) {
        if (event.signUpState.equals(MessageStatus.OK)) toMainActivity();
    }

    @Subscribe
    public void onSignUpLoginBusy(@NonNull SignUpFreeLogin event) {
        setIcon(loginIcUserName, event.isLoginFree);
    }

    public void setIcon(ImageView image, boolean status) {
        image.setVisibility(View.VISIBLE);
        if (status) image.setImageDrawable(icLoginOk);
        else image.setImageDrawable(icLoginCancel);
    }

    public void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.KEY_USER_NAME, loginUserName.getText().toString());
        SharedPreferences preff = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preff.edit();
        edit.putString("UserName",loginUserName.getText().toString());
        edit.putString("UserPass",loginPassword.getText().toString());
        edit.commit();
        startActivity(intent);
        BusProvider.getInstance().unregister(this);
        this.finish();
    }

    @Override
    protected void onStop() {
//        BusProvider.getInstance().unregister(this);
        super.onStop();
    }
}
