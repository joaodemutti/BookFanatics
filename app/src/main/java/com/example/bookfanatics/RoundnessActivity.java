package com.example.bookfanatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RoundnessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roundness);


        BottomNavigationView bottomBar = (BottomNavigationView) findViewById(R.id.SharedLayout_bottombar);
        bottomBar.setSelectedItemId(R.id.nav_menuitem_roundness);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Class activityClass;
                switch (item.getItemId()) {
                    case R.id.nav_menuitem_main:
                        activityClass = MainActivity.class;
                        break;
                    case R.id.nav_menuitem_librarylist:
                        activityClass = LibraryListActivity.class;
                        break;
                    case R.id.nav_menuitem_mylibraries:
                        activityClass = MyLibrariesActivity.class;
                        break;
                    default:
                        activityClass = null;
                        break;
                }
                if (activityClass != null && getBaseContext().getClass() != activityClass) {
                    startActivity(new Intent(getApplicationContext(), activityClass));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    return true;
                }
                return false;
            }});
    }
}
