package com.example.bookfanatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookfanatics.code.StaticEnviroment;
import com.example.bookfanatics.system.Library;
import com.example.bookfanatics.code.LibraryRecyclerAdapter;
import com.example.bookfanatics.system.Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryListActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener {

    public static final String
            LIBRARYID_INTENT_EXTRA = "com.example.bookfanatics.LIBRARYID_INTENT_EXTRA";
    String radiusQuery="50000";
    public final static int
            REQUEST_LOCATION_PERMISSION = 1342,
            REQUEST_LOCATION_PERMISSION_BETA = 4321;
    public int index = 0;
    public final int maximunResults = 100;

    static double latitude,longitude;
    static int length;

    RequestQueue queue;
    ExecutorService executor;

    RecyclerView librariesView;
    LinearLayoutManager lLayoutManager;
    BottomNavigationView bottomBar;
    View noResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarylist);

        length = getResources().getInteger(R.integer.librarylist_query_size);

        noResult = findViewById(R.id.LibraryList_error_text);
        librariesView = (RecyclerView) findViewById(R.id.LibrayList_libraries_recycler);

        lLayoutManager = new LinearLayoutManager(this);
        lLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        findViewById(R.id.BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocationLibraries();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);

        }

        bottomBar = (BottomNavigationView) findViewById(R.id.SharedLayout_bottombar);
        bottomBar.setSelectedItemId(R.id.nav_menuitem_librarylist);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Class activityClass;
                switch (item.getItemId()) {
                    case R.id.nav_menuitem_main:
                        activityClass = MainActivity.class;
                        break;
                    case R.id.nav_menuitem_mylibraries:
                        activityClass = MyLibrariesActivity.class;
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

        if(getIntent().getBundleExtra(MainActivity.SEARCH_VALUE_PARAM)!=null)
        {
            try{
                Bundle bundle = getIntent().getBundleExtra(MainActivity.SEARCH_VALUE_PARAM);
                latitude = bundle.getDouble("latitude");
                longitude = bundle.getDouble("longitude");
                requestLibraries(String.valueOf(latitude), String.valueOf(longitude));
            }
            catch(Exception e)
            {
                Toast.makeText(this,"Can't search",Toast.LENGTH_LONG).show();
            }
        }
    }


    public void getLastLocationLibraries() {

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        try
        {
            FusedLocationProviderClient flpClient;
            flpClient = LocationServices.getFusedLocationProviderClient(this);
            Task<Location> task = flpClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    List<Address> addresses;
                    if(location==null)return;
                    LibraryListActivity.longitude = location.getLongitude();
                    LibraryListActivity.latitude = location.getLatitude();

                    requestLibraries(String.valueOf(latitude),String.valueOf(longitude));
                    }
                });

        }
           catch(Exception e){
               noResult(true);
           }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:

                if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(
                                this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_LOCATION_PERMISSION_BETA);
            break;

            case REQUEST_LOCATION_PERMISSION_BETA:
                if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    LibraryListActivity.latitude = 300;
                    LibraryListActivity.longitude = 300;
                }//invalid coordinates
            break;
        }

    }

    public void requestLibraries(@NonNull String latitudeQuery, @NonNull String longitudeQuery)
    {
        if(noResult())
            noResult(false);
        double lat=Double.parseDouble(latitudeQuery);
        double lng=Double.parseDouble(longitudeQuery);
        if(lat>90||lat<-90||lng>180||lng<-180)
        {
            Toast.makeText(this,"Invalid coordinates",Toast.LENGTH_LONG);
            noResult(true);
            return;
        }

        queue = Volley.newRequestQueue(this);
        executor = Executors.newSingleThreadExecutor();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latitudeQuery+","+longitudeQuery+"&radius="+radiusQuery+"&type=library&key="+getString(R.string.app_api_key);
        JsonObjectRequest request;

        request = new JsonObjectRequest(Request.Method.GET, url, null, this, null);

        queue.add(request);
    }

    @Override
    public void onResponse(JSONObject result)
    {


        try {
            JSONArray results = result.getJSONArray("results");



            Library[] mylibrary = new Library[results.length()];
            for(int i = 0;i<results.length(); i++)
            {
                JSONObject library = results.getJSONObject(i);
                JSONObject location = library.getJSONObject("geometry").getJSONObject("location");
                double rate;
                try {
                    rate = library.getDouble("rating");
                }catch (Exception e){rate=0;}

                mylibrary[i] = new Library(
                        library.get("vicinity")+"",
                        library.get("name")+"",
                        Double.valueOf(location.get("lat")+""),
                        Double.valueOf(location.get("lng")+""),
                        rate
                    );
                mylibrary[i].status = library.get("business_status")+"";
            }

            LibraryRecyclerAdapter recyclerAdapter = new LibraryRecyclerAdapter(this, mylibrary,latitude,longitude,length);

            librariesView.setAdapter(recyclerAdapter);;
            librariesView.setLayoutManager(lLayoutManager);
            librariesView.getAdapter().notifyDataSetChanged();

        }
        catch (JSONException ex) {
            noResult(true);
            Toast.makeText(this, "Reposta inválida do servidor"+ex.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    public void noResult(boolean bool)
    {
      if(bool)noResult.setVisibility(View.GONE);
      noResult.setVisibility(View.VISIBLE);
    }
    public boolean noResult()
    {
        return noResult.getVisibility()==View.VISIBLE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(librariesView.getAdapter()!=null) {
            if (((LibraryRecyclerAdapter) librariesView.getAdapter()).visibleProgress != null)
                ((LibraryRecyclerAdapter) librariesView.getAdapter()).visibleProgress.setVisibility(View.GONE);
        }
        bottomBar.setSelectedItemId(R.id.nav_menuitem_librarylist);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("user", StaticEnviroment.UserLogin);
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
