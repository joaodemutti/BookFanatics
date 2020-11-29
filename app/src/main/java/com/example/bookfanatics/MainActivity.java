package com.example.bookfanatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookfanatics.code.DatabaseHelper;
import com.example.bookfanatics.code.StaticEnviroment;
import com.example.bookfanatics.system.Login;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity{
    public static final String SEARCH_VALUE_PARAM = "com.example.bookfanatics.SEARCH_VALUE";
    public static final String SEARCH_FILENAME = "SEARCH_HISTORY";
    public static final String LOGIN_FILENAME = "Login";

    TextView searchText;
    BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(StaticEnviroment.UserLogin ==null || intent.getIntExtra(LauncherActivity.LOGINID_INTENT_EXTRA,0) != 0)
        {
            DatabaseHelper db = new DatabaseHelper(this);
            int id = intent.getIntExtra(LauncherActivity.LOGINID_INTENT_EXTRA,0);
            Log.println(Log.DEBUG,"UserID", "newID:"+String.valueOf(id));

            StaticEnviroment.UserLogin = db.getLogin(id);

            if(StaticEnviroment.UserLogin==null || id == 0)
            {
                Toast.makeText(this,getText(R.string.invalid_loginid_error),Toast.LENGTH_LONG).show();
                finish();
            }
            boolean wantPassword = intent.getBooleanExtra(LauncherActivity.WANTSAVE_INTENT_EXTRA, false);
            if(wantPassword)
            {

                Log.d("sharedPreferenceJSON","wantPassword");
                SharedPreferences.Editor setting;
                setting = getSharedPreferences(LauncherActivity.SAVEPASSWORD_PREFERENCE,0).edit();
                setting.putBoolean("passwordSaved",true);
                setting.commit();


                File file = new File(this.getFilesDir(),LOGIN_FILENAME);
                try{
                    Log.d("createFileJSON","beforeExists");
                    if(!file.exists())
                    {
                        boolean created;
                        created = file.createNewFile();
                        if(created)Log.d("createFileJSON",created?"sucess":"fail");
                    }

                }
                catch (IOException e){Toast.makeText(this,"creationError  "+e.getMessage(),Toast.LENGTH_LONG).show();}

                Log.d("writeFileJSON","TryWrite");
                try {
                    FileOutputStream fos =  new FileOutputStream(file);
                    ObjectOutputStream outputStream = new ObjectOutputStream(fos);
                    Serializable login = StaticEnviroment.UserLogin;
                    outputStream.writeObject(login);
                    fos.close();
                    outputStream.close();
                    Log.d("writeFileJSON","ProbalyWrited");
                } catch (Exception e) {
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }
        else {
            DatabaseHelper db = new DatabaseHelper(this);
            Login login = db.getLogin(StaticEnviroment.UserLogin.getid());
            if(login==null){
                Toast.makeText(this,getText(R.string.invalid_loginid_error),Toast.LENGTH_LONG).show();
                SharedPreferences.Editor preference = getSharedPreferences(LauncherActivity.SAVEPASSWORD_PREFERENCE,0).edit();
                preference.putBoolean("passwordSaved",false);
                preference.commit();
                finish();
            }
            Log.println(Log.DEBUG,"UserID", "ID:"+String.valueOf(StaticEnviroment.UserLogin.getid()));
        }



        searchText = (TextView) findViewById(R.id.main_search_txt);
        Toolbar toolBar = (Toolbar) findViewById(R.id.MainActivity_toolbar);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            }
        });

        bottomBar = (BottomNavigationView) findViewById(R.id.SharedLayout_bottombar);
        bottomBar.setSelectedItemId(R.id.nav_menuitem_main);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Class activityClass;
                switch (item.getItemId()) {
                    case R.id.nav_menuitem_librarylist:
                        activityClass = LibraryListActivity.class;
                        break;
                    case R.id.nav_menuitem_roundness:
                        activityClass = RoundnessActivity.class;
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
            }
        });

    }

    public void onSearch(View view)
    {
           /* File file = new File(getFilesDir(),SEARCH_FILENAME);
            FileInputStream inputStream;
            Toast.makeText(this,"...",Toast.LENGTH_LONG);
            try {
                inputStream= new FileInputStream(file);
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                try (BufferedReader reader = new BufferedReader(streamReader)) {
                    String string = reader.readLine();
                    String result = "";
                    int i =0;
                    while(string != null){
                        result+=string;
                        string = reader.readLine();i++;
                    }
                    //((TextView)findViewById()).setText(result);
                }
            }
            catch (Exception e) {//nothing happens }


        file = new File(this.getCacheDir(),SEARCH_FILENAME);
        try{
            if(!file.exists())
                file.createNewFile();
        }
        catch (IOException e){Toast.makeText(this,"creationError  "+e.getMessage(),Toast.LENGTH_LONG).show();}

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write((myText+"\n").getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }*/

        String myText = searchText.getText().toString();
        if(myText==null|| myText.equals(""))return;

        Address address;
        try {
            address = new Geocoder(this).getFromLocationName(myText,1).get(0);
            if(address==null)throw new IOException();
        }
        catch (Exception e) {
            Toast.makeText(this,"GeoLocation search error, try a different value", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this,LibraryListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude",address.getLatitude());
        bundle.putDouble("longitude",address.getLongitude());
        intent.putExtra(SEARCH_VALUE_PARAM, bundle);
        String stgCoor = String.valueOf(address.getLatitude())+" "+String.valueOf(address.getLongitude());
        Toast.makeText(this,stgCoor,Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(()->{
            startActivity(intent);
        },1500);

    }



    /*
    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(("teste\n").getBytes());
                    outputStream.close();
                    Toast.makeText(this,"WRITED ",Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
    */

    @Override
    protected void onResume() {
        super.onResume();
        bottomBar.setSelectedItemId(R.id.nav_menuitem_main);
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
