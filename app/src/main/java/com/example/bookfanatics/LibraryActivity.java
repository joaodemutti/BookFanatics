package com.example.bookfanatics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookfanatics.code.DatabaseHelper;
import com.example.bookfanatics.code.LibrarySlideAdapter;
import com.example.bookfanatics.code.StaticEnviroment;
import com.example.bookfanatics.system.FanaticsClass;
import com.example.bookfanatics.system.Library;
import com.example.bookfanatics.system.Login;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import static com.example.bookfanatics.LibraryListActivity.LIBRARYID_INTENT_EXTRA;


public class LibraryActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 9999;
    public String shareMessage;
    static final  String COLOR_HEART = "STRINGHEART";
    static final  String COLOR_SHARE = "STRINGSHARE";

    public Library library;
    FanaticsClass relation;
    DatabaseHelper db;
    File currentFilePath;
    String currentFileName,currentStringUri;
    Uri currentUri;

    boolean favBool,sharedBool;
    ImageView shareView, favoriteView;
    TextView addressText,nameText;
    ViewPager2 pagerView;
    Address address;
    BottomNavigationView bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        shareMessage=getResources().getString(R.string.share_message);

        favBool = false;

        favoriteView = (ImageView) findViewById(R.id.Library_favorite_button);
        shareView = (ImageView) findViewById(R.id.Library_share_button);
        addressText = (TextView) findViewById(R.id.LibraryActivity_address);
        nameText = (TextView) findViewById(R.id.library_text_name);
        pagerView = (ViewPager2) findViewById(R.id.library_pager_slide);

        int id = getIntent().getExtras().getInt(LIBRARYID_INTENT_EXTRA);
        db = new DatabaseHelper(this);
        library = db.getLibrary(id);
        Log.d("findRelation","libraryID:"+String.valueOf(library.id));
        Log.d("findRelation","userID:"+String.valueOf(StaticEnviroment.UserLogin.getid()));

        FanaticsClass relationHolder = new FanaticsClass();
        relationHolder = db.findRelation(StaticEnviroment.UserLogin.getid(),library.id);

        Log.d("findRelation", "relationID:"+String.valueOf(relationHolder!=null?relationHolder.id:0));

        if(relationHolder!=null)
        {
            relation = relationHolder;
            Log.d("referenceRelation", "relationID:"+String.valueOf(relation.id));

        }
        else
        {
            relation = new FanaticsClass(StaticEnviroment.UserLogin.getid(),library.id);
            relation.id = db.insertRelation(relation);
            relation.setHasShared(0);
            relation.setLocationOpened(0);
            relation.setIsFavorite(0);
            Log.d("makeRelation", "relationID:"+String.valueOf(relation.id));
        }

        favBool = relation.isFavorite>0;
        sharedBool = relation.hasShared>0;
        relation.library = library;
        relation.login = StaticEnviroment.UserLogin;
        addressText.setText(library.address);
        nameText.setText(library.name);

        setSlideImages();

        try {
            address = new Geocoder(this).getFromLocationName(library.address,1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }



        bottomBar = (BottomNavigationView) findViewById(R.id.Library_navbar_include);
        bottomBar.setSelected(false);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(favBool) favoriteView.setColorFilter(Color.RED);
        else favoriteView.setColorFilter(Color.BLACK);
        if(sharedBool) shareView.setColorFilter(Color.BLUE);
        else shareView.setColorFilter(Color.BLACK);

    }

    public void onCamera(View view)
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);

        String filename = StaticEnviroment.UserLogin.getusername()+"-"+library.name.replace(" ","_")+".png";
    File filepath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if(intent.resolveActivity(getPackageManager())!=null)
    {
        File file = new File(filepath,filename);
        int i = 1;
        while(file.exists())
        {
            filename = filename.replace((i==1?"":String.valueOf(i-1))+".",String.valueOf(i)+".");
            file = new File(filepath,filename);
            i++;
        }
        boolean created = false;
        try {
            Log.d("imageFileName",filename);
            Log.d("imageFilePath",filepath.getPath());
            created = file.createNewFile();
        } catch (Exception e) {
            created = false;
        }
        if(created)
        {
            currentFileName = filename;
            currentFilePath = filepath;
            //MediaStore.Images.Media.
            //Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//MediaStore.Images.Media.getContentUri(filename.replace(".png",""));
            //currentUri = uri;
            //File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            Uri uri = FileProvider.getUriForFile(this,"com.example.android.fileprovider",file);
            currentUri = uri;
            ContentValues values = new ContentValues();
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);//MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,0);
        }

    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File file = new File(currentFilePath, currentFileName);
        if(resultCode == RESULT_OK) {
            Log.d("FILEIMAGERESULT","CHECK_EXISTS");
                if (file.exists()) {
                    Log.d("FILEIMAGERESULT","EXISTS!");
                    int id = db.insertImage(relation.id,currentFilePath,currentFileName);
                    File thumb = new File(currentFilePath,currentFileName.replace(".png","thumb.png"));
                    if(!thumb.exists()) {
                        try {
                            thumb.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(thumb);
                        scaleDown(BitmapFactory.decodeFile(file.getPath()),250,false).compress(Bitmap.CompressFormat.PNG,0,fos);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.d("insertIMAGE","id: "+String.valueOf(id));
                }else{
                    Toast.makeText(this, getString(R.string.create_image_error), Toast.LENGTH_LONG).show();
                }
            setSlideImages();
            Log.d("IMAGERESULT", "OKEND");
        }
        else if (file.exists())file.delete();
        }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean(COLOR_HEART,favBool);
        outState.putBoolean(COLOR_SHARE,sharedBool);

        outState.putSerializable("user",StaticEnviroment.UserLogin);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        favBool = savedInstanceState.getBoolean(COLOR_HEART);
        sharedBool = savedInstanceState.getBoolean(COLOR_SHARE);
        if(favBool) favoriteView.setColorFilter(Color.RED);
        else favoriteView.setColorFilter(Color.BLACK);
        if(sharedBool) shareView.setColorFilter(Color.BLUE);
        else shareView.setColorFilter(Color.BLACK);

        Login login = (Login)savedInstanceState.getSerializable("user");
        if(login!=null)
        {
            StaticEnviroment.UserLogin = login;
        }
    }

    public void onFavorite(View view)
    {
        favBool = !favBool;

        ImageView heart = (ImageView) view;
        if(favBool)
        {
            heart.setColorFilter(Color.RED);
            relation.setIsFavorite(1);
        }
        else
        {
            heart.setColorFilter(Color.BLACK);
            relation.setIsFavorite(0);
        }
        db.updateRelation(relation);
    }

    public void mapIntent(View view) throws IOException {

        relation.setLocationOpened((int) Calendar.getInstance().getTime().getTime());
        db.updateRelation(relation);

        Uri location = Uri.parse("geo:"+address.getLatitude()+","+address.getLongitude()+"?q="+address.getAddressLine(0).replaceAll(" ","+"));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent );

    }

    public void onShareIntent(View view) throws IOException {

        relation.setHasShared(1);
        db.updateRelation(relation);
        sharedBool = true;
        shareView.setColorFilter(Color.BLUE);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage+"\n\n"+library.name+":\nhttps://maps.google.com/maps?q="+library.address.replace(" ","+")+"\n"+getString(R.string.app_name)+" app");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void setSlideImages(){
        List<ContentValues> valuesList = db.getImages(relation.id);
        if(valuesList != null) {
            Log.d("setFileIMAGEs","imagesDB: "+String.valueOf(valuesList.size()));
            Bitmap[] bitmapList = new Bitmap[valuesList.size()];
            Bitmap[] thumbList = new Bitmap[valuesList.size()];
            ContentValues[] dbReferences = new ContentValues[valuesList.size()];
            String filepathKey = "(File)filepath";
            for (int i = 0; i < valuesList.size(); i++) {
                Log.d("setFileIMAGEs","image"+String.valueOf(i)+": "+valuesList.get(i).getAsString("filename"));
                bitmapList[i] = openImageFile(valuesList.get(i).getAsString(DatabaseHelper.IMAGE_FILEPATH_attr), valuesList.get(i).getAsString(DatabaseHelper.IMAGE_FILENAME_attr));
                thumbList[i] = openImageFile(valuesList.get(i).getAsString(DatabaseHelper.IMAGE_FILEPATH_attr), valuesList.get(i).getAsString(DatabaseHelper.IMAGE_FILENAME_attr).replace(".png","thumb.png"));
                dbReferences[i] = valuesList.get(i);
            }
            LibrarySlideAdapter slideAdapter = new LibrarySlideAdapter(this, bitmapList, thumbList,dbReferences);
            pagerView.setAdapter(slideAdapter);
            pagerView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    if(pagerView.findViewWithTag("imageTag"+String.valueOf(((LibrarySlideAdapter)pagerView.getAdapter()).lastItem)).getVisibility() != View.GONE){
                        if(pagerView.getScrollState() == ViewPager2.SCROLL_STATE_IDLE)//HighResolution Image
                            ((ImageView)pagerView.findViewWithTag("imageTag"+String.valueOf(((LibrarySlideAdapter)pagerView.getAdapter()).lastItem))).setImageBitmap(((LibrarySlideAdapter)pagerView.getAdapter()).imageList[((LibrarySlideAdapter)pagerView.getAdapter()).lastItem]);
                        else//LowResolution Thumb
                            ((ImageView)pagerView.findViewWithTag("imageTag"+String.valueOf(((LibrarySlideAdapter)pagerView.getAdapter()).lastItem))).setImageBitmap(((LibrarySlideAdapter)pagerView.getAdapter()).imageThumbList[((LibrarySlideAdapter)pagerView.getAdapter()).lastItem]);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    ((LibrarySlideAdapter)pagerView.getAdapter()).lastItem = position;
                }
            });

            pagerView.getAdapter().notifyDataSetChanged();
        }
    }
    public Bitmap openImageFile(String filepathParam,String filename){
        Log.d("openFileIMAGE",filepathParam+filename);
        File filepath = new File(filepathParam);
        File file = new File(filepath,filename);
       return BitmapFactory.decodeFile(file.getPath());
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

}
