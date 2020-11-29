package com.example.bookfanatics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bookfanatics.code.DatabaseHelper;
import com.example.bookfanatics.code.LoginRecycler;
import com.example.bookfanatics.system.Login;

public class LoginManagerActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginmanager);
        recyclerView = (RecyclerView) findViewById(R.id.loginmanager_recylcer);
        DatabaseHelper db = new DatabaseHelper(this);
        Login[] loginList = db.getAllLogin();
        if(loginList!=null) {
            LoginRecycler adapter = new LoginRecycler(this, loginList);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(llm);
            recyclerView.getAdapter().notifyDataSetChanged();
}
        else Toast.makeText(this,getString(R.string.no_existing_login),Toast.LENGTH_LONG).show();
    }

    public void onAdd(View view){
        startActivity(new Intent(this,NewLoginActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,LauncherActivity.class));
    }
}