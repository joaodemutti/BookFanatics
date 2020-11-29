package com.example.bookfanatics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookfanatics.code.DatabaseHelper;
import com.example.bookfanatics.system.Login;

public class NewLoginActivity extends AppCompatActivity {

    EditText usernameText,emailText,passwordText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlogin);
        usernameText = (EditText) findViewById(R.id.newlogin_text_username);
        emailText = (EditText) findViewById(R.id.newlogin_text_email);
        passwordText = (EditText) findViewById(R.id.newlogin_text_password);
        button = (Button) findViewById(R.id.newlogin_button);
    }

    public void onNewLogin(View view) {
        DatabaseHelper db = new DatabaseHelper(this);

        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(username.length()<4 || email.length()<5 || password.length() < 4){
            Toast.makeText(this,getString(R.string.newlogin_validate_minimumlength),Toast.LENGTH_LONG).show();
            return;
        }
        if(db.findLogin(username)!=null)
        {
            Toast.makeText(this,getString(R.string.newlogin_validate_usernametaken),Toast.LENGTH_LONG).show();
            return;
        }
        if(db.findLogin(email)!=null)
        {
            Toast.makeText(this,getString(R.string.newlogin_validate_emailtaken),Toast.LENGTH_LONG).show();
            return;
        }

        Login login = new Login(
                usernameText.getText().toString(),
                emailText.getText().toString(),
                passwordText.getText().toString()
        );
        Integer id = db.insertLogin(login,password);
        String message  = getString(R.string.newlogin_created_message).replace("%username",username).replace("%email",email).replace("%id", String.valueOf(id));
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}