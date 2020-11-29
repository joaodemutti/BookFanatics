package com.example.bookfanatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;

import com.example.bookfanatics.code.DatabaseHelper;
import com.example.bookfanatics.code.LibraryRecyclerAdapter;
import com.example.bookfanatics.code.StaticEnviroment;
import com.example.bookfanatics.system.Library;
import com.example.bookfanatics.system.Login;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MyLibrariesActivity extends AppCompatActivity {

    RecyclerView librariesView;
    BottomNavigationView bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylibraries);
        DatabaseHelper db = new DatabaseHelper(this);
        List<Library> libraryList = db.libraryInRelation(StaticEnviroment.UserLogin.getid(),"isFavorite = 1");

        librariesView = (RecyclerView) findViewById(R.id.mylibraries_recycler);
        if(libraryList!=null)
        {
            Library[] libraries = new Library[libraryList.size()];
            LinearLayoutManager lLayoutManager = new LinearLayoutManager(this);
            lLayoutManager.setOrientation(RecyclerView.VERTICAL);
            libraries = libraryList.toArray(libraries);
            LibraryRecyclerAdapter recyclerAdapter = new LibraryRecyclerAdapter(this, libraries,0,libraryList.size());
            librariesView.setAdapter(recyclerAdapter);
            librariesView.setLayoutManager(lLayoutManager);
            librariesView.getAdapter().notifyDataSetChanged();
        }

        bottomBar = (BottomNavigationView) findViewById(R.id.SharedLayout_bottombar);
        bottomBar.setSelectedItemId(R.id.nav_menuitem_mylibraries);
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
                    case R.id.nav_menuitem_roundness:
                        activityClass = RoundnessActivity.class;
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
    @Override
    protected void onResume() {
        super.onResume();

        if(librariesView.getAdapter()!=null) {
            if (((LibraryRecyclerAdapter) librariesView.getAdapter()).visibleProgress != null)
                ((LibraryRecyclerAdapter) librariesView.getAdapter()).visibleProgress.setVisibility(View.GONE);
        }

        bottomBar.setSelectedItemId(R.id.nav_menuitem_mylibraries);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("user",StaticEnviroment.UserLogin);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Login login = (Login)savedInstanceState.getSerializable("user");
        if(login!=null)
        {
            StaticEnviroment.UserLogin = login;
        }
    }
}
