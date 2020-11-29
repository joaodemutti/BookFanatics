package com.example.bookfanatics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bookfanatics.code.DatabaseHelper;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onChangeUser(View view) {
        Intent intent = new Intent(this,LauncherActivity.class);
        intent.putExtra(LauncherActivity.EDITING_ACTIVITY_EXTRA,true);
        startActivity(intent);
    }

    public void onResetSQLite(View view) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.upgrade();
    }
}
