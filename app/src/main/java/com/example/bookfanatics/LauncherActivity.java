package com.example.bookfanatics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookfanatics.code.DatabaseHelper;
import com.example.bookfanatics.code.StaticEnviroment;
import com.example.bookfanatics.system.Login;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class LauncherActivity extends AppCompatActivity {

    public static String SAVEPASSWORD_PREFERENCE = "BookFanatics.savePassword_preference";

    public static String LOGINID_INTENT_EXTRA = "BookFanatics.LauncherActivity.loginID_extra",//sendingExtras
                        WANTSAVE_INTENT_EXTRA = "BookFanatics.LauncherActivity.wantSavePassword_extra";
    public static String EDITING_ACTIVITY_EXTRA = "BookFanatics.LauncherActivity.editing_extra";//gettingExtras

    boolean passwordSaved,wantSavePassword,editing;
    Login savedLogin;

    EditText loginEditText, passwordEditText;
    View progressView, loginButton,newLoginButton;
    CheckBox checkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        loginEditText = (EditText) findViewById(R.id.LauncherActivity_logintext);
        passwordEditText = (EditText) findViewById(R.id.launcherActivity_passwordtext);
        progressView = (View) findViewById(R.id.launcher_progress);
        loginButton = (View) findViewById(R.id.launcher_loginbutton);
        newLoginButton = (View) findViewById(R.id.launcher_newlogin_button);
        checkView = (CheckBox) findViewById(R.id.launcher_check_password);

        wantSavePassword = false;
        passwordSaved = getSharedPreferences(SAVEPASSWORD_PREFERENCE,0).getBoolean("passwordSaved",false);
        if(getIntent().getBooleanExtra(EDITING_ACTIVITY_EXTRA,false))
        {
            loginEditText.setText(StaticEnviroment.UserLogin.getusername());
            if(passwordSaved)
            {
                savedLogin = StaticEnviroment.UserLogin;
                checkView.setChecked(true);
                wantSavePassword = true;
            }
            else
            {
                checkView.setChecked(false);
                wantSavePassword = false;
            }
            passwordSaved = false;
            editing = true;
        }
        if(passwordSaved)
        {
            File file = new File(getFilesDir(),MainActivity.LOGIN_FILENAME);
            ObjectInputStream inputStream;
            Toast.makeText(this,"...",Toast.LENGTH_LONG);
            try {
                FileInputStream fis = new FileInputStream(file);
                inputStream= new ObjectInputStream(fis);
                savedLogin = (Login) inputStream.readObject();
                inputStream.close();
                fis.close();
            }
            catch (Exception e) {
                Log.e("userJSON",e.getMessage());
            }
            if(savedLogin==null)passwordSaved=false;
            else loginEditText.setText(savedLogin.getusername());
        }

        if(!passwordSaved)
        {
            loginEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            passwordEditText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            checkView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);
            newLoginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("username",loginEditText.getText().toString());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        loginEditText.setText(savedInstanceState.getString("username"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(passwordSaved) {
            StaticEnviroment.UserLogin = savedLogin;
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            Toast.makeText(this, savedLogin.getemail(), Toast.LENGTH_SHORT).show();
            progressView.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, 1000);
        }else
        {

        }
    }

    public void onLogin(View view) {
        progressView.setVisibility(View.VISIBLE);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String username = loginEditText.getText().toString();
        if(username!=null && username!="") {

            Login login = dbHelper.findLogin(username);
            String password = passwordEditText.getText().toString();
            if (password != null&&login!=null) {
                if (login.validatePassword(password)) {

                    Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                    intent.putExtra(LOGINID_INTENT_EXTRA,login.getid());
                    intent.putExtra(WANTSAVE_INTENT_EXTRA,wantSavePassword);

                    Toast.makeText(this, !username.equals(login.getusername()) ?login.getusername():login.getemail(),Toast.LENGTH_SHORT).show();
                    progressView.setVisibility(View.INVISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    }, 500);
                }
                else {
                    progressView.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, getText(R.string.invalid_login_error), Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                progressView.setVisibility(View.INVISIBLE);
                Toast.makeText(this, getText(R.string.invalid_login_error), Toast.LENGTH_LONG).show();
            }
        }
        }

        public void onWantSavePassword(View view)
        {
            wantSavePassword = ((CheckBox)view).isChecked();
            if(!wantSavePassword&&editing)
            {
                SharedPreferences.Editor setting;
                setting = getSharedPreferences(SAVEPASSWORD_PREFERENCE,0).edit();
                setting.putBoolean("passwordSaved",false);
                editing = false;
                setting.apply();
            }
        }

        public void onLoginManager(View view){
        startActivity(new Intent(this,LoginManagerActivity.class));
        }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
