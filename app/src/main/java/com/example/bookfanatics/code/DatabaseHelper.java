package com.example.bookfanatics.code;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bookfanatics.R;
import com.example.bookfanatics.system.FanaticsClass;
import com.example.bookfanatics.system.Library;
import com.example.bookfanatics.system.Login;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BookFanatics.db";
    public static final int DOUBLE_CONVERT = 100000;
    public static final String IMAGE_FILEPATH_attr = "filepath",
                                IMAGE_FILENAME_attr = "filename";
    public Context context;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table login (" +
            "id integer primary key," +
            " username text unique," +
            " email text unique," +
            " password text not null)");

    db.execSQL("create table library (" +
            "id integer primary key," +
            " name text not null," +
            " address text unique," +
            " latitude integer not null," +
            " longitude integer not null," +
            " rate real)");

    db.execSQL("create table relation (" +
            "id integer primary key" +
            ", loginID integer not null," +
            " libraryID integer not null," +
            " hasShared integer," +
            " locationOpened integer,"+
            " isFavorite integer," +
            " foreign key (loginID) references login(id)," +
            " foreign key (libraryID) references login(id))");

    db.execSQL("create table image (" +
            "id integer primary key," +
            " relationID integer not null," +
            " filename text not null," +
            " filepath text not null," +
            " foreign key (relationID) references relation(id))");


    ContentValues devLogin = new ContentValues();
    devLogin.put("id",66);
    devLogin.put("username","joaodemutti");
    devLogin.put("email","joaofacindemutti@gmail.com");
    devLogin.put("password","joao2003");
    db.insert("login",null, devLogin);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    db.execSQL("drop table if exists image");
    db.execSQL("drop table if exists relation");
    db.execSQL("drop table if exists library");
    db.execSQL("drop table if exists login");
    onCreate(db);
    }

    public Login findLogin(String useremailname){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from login where username like '"+useremailname+"' or email like '"+useremailname+"'",null);

        if(!(cursor.getCount() >0))
        {

            if(!cursor.isClosed())cursor.close();
            return null;

        }
        Login login;

        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            login = new Login(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getString(cursor.getColumnIndex("email")),
                    cursor.getString(cursor.getColumnIndex("password"))
            );

            if(!cursor.isClosed())cursor.close();
            return login;
        }
        if(!cursor.isClosed())cursor.close();
        return null;
    }

    public Library findLibrary(String address){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from library where address like '"+address+"'",null);

        cursor.moveToFirst();

        Library library;

        if(!cursor.isAfterLast()) {
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))/DOUBLE_CONVERT;
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))/DOUBLE_CONVERT;

            library = new Library(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("address")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    latitude,
                    longitude,
                    cursor.getDouble(cursor.getColumnIndex("rate"))
            );

            if(!cursor.isClosed())cursor.close();
            return library;
        }
        if(!cursor.isClosed())cursor.close();
        return null;
    }

    public Library getLibrary(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from library where id = "+String.valueOf(id),null);

        cursor.moveToFirst();
        Library library;

        if(!cursor.isAfterLast()) {
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))/DOUBLE_CONVERT;
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))/DOUBLE_CONVERT;

            library = new Library(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("address")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    latitude,
                    longitude,
                    cursor.getDouble(cursor.getColumnIndex("rate"))
            );

            if(!cursor.isClosed())cursor.close();
            return library;
        }
        if(!cursor.isClosed())cursor.close();
        return null;
    }

    public Login getLogin(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from login where id = "+String.valueOf(id),null);

        cursor.moveToFirst();
        Login login;

        if(!cursor.isAfterLast()) {
            login = new Login(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getString(cursor.getColumnIndex("email")),
                    cursor.getString(cursor.getColumnIndex("password"))
            );

            if(!cursor.isClosed())cursor.close();
            return login;
        }
            if(!cursor.isClosed())cursor.close();
            return null;
    }

    public int insertLibrary(Library library) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select MAX(id) as idMax from library",null);

        int ocupiedID;
        if(!(cursor.getCount()>0))ocupiedID=0;
        else{
            cursor.moveToFirst();
            ocupiedID = cursor.getInt(cursor.getColumnIndex("idMax"));
        }
        if(!cursor.isClosed())cursor.close();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",ocupiedID+1);
        values.put("address",library.address);
        values.put("name",library.name);
        values.put("latitude",(int)(library.latitude*DOUBLE_CONVERT));
        values.put("longitude",(int)(library.longitude*DOUBLE_CONVERT));
        values.put("rate",library.rate);
        db.insert("library",null, values);
        return values.getAsInteger("id");
    }

    public FanaticsClass findRelation(int loginID, int libraryID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from relation where (loginID = "+String.valueOf(loginID)+") and (libraryID = "+String.valueOf(libraryID)+")",null);
        FanaticsClass result = new FanaticsClass(loginID,libraryID);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            result.id = cursor.getInt(cursor.getColumnIndex("id"));
            result.isFavorite = cursor.getInt(cursor.getColumnIndex("isFavorite"));
            result.hasShared = cursor.getInt(cursor.getColumnIndex("hasShared"));
            result.locationOpened = cursor.getInt(cursor.getColumnIndex("locationOpened"));
        }
        if(!cursor.isClosed())cursor.close();
        if(result.id>0)
        {
            return result;
        }
        else return null;
    }

    public Integer insertRelation(FanaticsClass relation)
    {
        if(!(relation.loginID > 0) || !(relation.libraryID > 0))return null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id from library where id ="+String.valueOf(relation.libraryID),null);
        if(cursor.getCount()>0)
        {
            cursor = db.rawQuery("select id from login where id = "+String.valueOf(relation.loginID),null);
            if(cursor.getCount()>0) {
                cursor.close();
                cursor = db.rawQuery("select MAX(id) as idMax from relation",null);
                int ocupiedID;
                if(cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    ocupiedID = cursor.getInt(cursor.getColumnIndex("idMax"));
                }
                else ocupiedID =0;
                if(!cursor.isClosed())cursor.close();
                db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id",ocupiedID+1);
                values.put("libraryID",relation.libraryID);
                values.put("loginID",relation.loginID);
                if(relation.hasShared==1)values.put("hasShared",relation.hasShared);
                else values.put("hasShared",0);
                if(relation.isFavorite==1)values.put("isFavorite",relation.isFavorite);
                else values.put("isFavorite",0);
                if(relation.locationOpened==1)values.put("locationOpened",relation.locationOpened);
                else values.put("locationOpened",0);
                db.insert("relation",null,values);
                return values.getAsInteger("id");
            }
        }
    return null;
    }
    public void upgrade() {
        onUpgrade(this.getWritableDatabase(),1,1);
    }

    public void updateRelation(FanaticsClass relation) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("id",relation.id);
    values.put("libraryID",relation.libraryID);
    values.put("loginID",relation.loginID);
    values.put("hasShared",relation.hasShared);
    values.put("isFavorite",relation.isFavorite);
    values.put("locationOpened",relation.locationOpened);
    db.update("relation",values,"id = "+String.valueOf(relation.id),null);
    Log.d("updateRelation","id: "+String.valueOf(relation.id)+"; hasShared: "+String.valueOf(relation.hasShared)+"; isFavorite: "+String.valueOf(relation.isFavorite)+"; locationOpened: "+String.valueOf(relation.locationOpened));
    }

    public List<Library> libraryInRelation(int loginID)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from library where id in (select libraryID from relation where loginID =" +String.valueOf(loginID)+")",null);
        if(cursor.getCount()>0)
        {
            List<Library> libraryList = new ArrayList<Library>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                libraryList.add(
                        new Library(
                                cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("address")),
                                cursor.getString(cursor.getColumnIndex("name")),
                                cursor.getInt(cursor.getColumnIndex("latitude"))*DOUBLE_CONVERT,
                                cursor.getInt(cursor.getColumnIndex("longitude"))*DOUBLE_CONVERT,
                                cursor.getDouble(cursor.getColumnIndex("rate"))));
                cursor.moveToNext();
            }
            cursor.close();
            return  libraryList;
        }
        return null;
    }
    public List<Library> libraryInRelation(int loginID,String relationWhereClasule)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from library where id in (select libraryID from relation where loginID =" +String.valueOf(loginID)+" and "+relationWhereClasule+")",null);
        if(cursor.getCount()>0)
        {
            List<Library> libraryList = new ArrayList<Library>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                libraryList.add(
                        new Library(
                                cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("address")),
                                cursor.getString(cursor.getColumnIndex("name")),
                                cursor.getInt(cursor.getColumnIndex("latitude"))*DOUBLE_CONVERT,
                                cursor.getInt(cursor.getColumnIndex("longitude"))*DOUBLE_CONVERT,
                                cursor.getDouble(cursor.getColumnIndex("rate"))));
                cursor.moveToNext();
            }
            cursor.close();
            return  libraryList;
        }
        return null;
    }

    public int insertImage(int relationID, File filepath, String filename) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select MAX(id) as idMax from image",null);

        int ocupiedID;
        if(!(cursor.getCount()>0))ocupiedID=0;
        else{
            cursor.moveToFirst();
            ocupiedID = cursor.getInt(cursor.getColumnIndex("idMax"));
        }
        if(!cursor.isClosed())cursor.close();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",ocupiedID+1);
        values.put("relationID",relationID);
        values.put("filepath",filepath.getPath());
        values.put("filename",filename);
        db.insert("image",null, values);
        return values.getAsInteger("id");
    }


    public List<ContentValues> getImages(int relationID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from image where relationID = "+String.valueOf(relationID),null);
        if(cursor.getCount()>0)
        {
            List<ContentValues> valuesList = new ArrayList<ContentValues>(cursor.getCount());
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                ContentValues values = new ContentValues();
                values.put("id",cursor.getInt(cursor.getColumnIndex("id")));
                values.put("relationID",cursor.getInt(cursor.getColumnIndex("relationID")));
                values.put("filepath",cursor.getString(cursor.getColumnIndex("filepath")));
                values.put("filename",cursor.getString(cursor.getColumnIndex("filename")));
                valuesList.add(values);
                cursor.moveToNext();
            }
            if(!cursor.isClosed())cursor.close();
            return valuesList;
        }
    return null;
    }

    public int removeImage(int id) {
    SQLiteDatabase db = getWritableDatabase();
    return db.delete("image","id = "+String.valueOf(id),null);
    }

    public int removeLogin(int logintID) {
        SQLiteDatabase db = getWritableDatabase();
        int result = 0;
        result += db.delete("image", context.getString(R.string.sqlite_relation1),new String[]{String.valueOf(logintID)});
        result += db.delete("relation","loginID = ?",new String[]{String.valueOf(logintID)});
        return result + db.delete("login","id = "+String.valueOf(logintID),null);
    }

    public Login[] getAllLogin() {
    SQLiteDatabase db = getReadableDatabase();
    Cursor cursor = db.rawQuery("select * from login",null);
    int length = cursor.getCount();
    Login[] result = new Login[length];
    if(length>0) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
             result[cursor.getPosition()] = new Login(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("username")),
                    cursor.getString(cursor.getColumnIndex("email")),
                    cursor.getString(cursor.getColumnIndex("password"))
            );
            cursor.moveToNext();
        }
        return result;
    }
    return null;
    }

    public Integer insertLogin(Login login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select MAX(id) as idMax from login",null);

        int ocupiedID;
        if(!(cursor.getCount()>0))ocupiedID=0;
        else{
            cursor.moveToFirst();
            ocupiedID = cursor.getInt(cursor.getColumnIndex("idMax"));
        }
        if(!cursor.isClosed())cursor.close();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",ocupiedID+1);
        values.put("username",login.getusername());
        values.put("email",login.getemail());
        values.put("password",password);
        db.insert("login",null, values);
        return values.getAsInteger("id");
    }
}
