package com.example.bookfanatics.code;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfanatics.R;

import java.io.File;

public class LibrarySlideAdapter extends RecyclerView.Adapter<LibrarySlideAdapter.Holder> {
    Context context;
    public Bitmap[] imageList;
    LayoutInflater inflater;
    ContentValues[] imagesDBReference;
    public Bitmap[] imageThumbList;
    public int lastItem;

    public LibrarySlideAdapter(Context context, Bitmap[] imageList,Bitmap[] imageThumbList, ContentValues[] imagesDBReference){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.imageList = imageList;
        this.imagesDBReference = imagesDBReference;
        this.imageThumbList = imageThumbList;
        lastItem = 0;
    }

    @NonNull
    @Override
public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.pageritem_library_photo,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bool = imageList[position]!=null;
        if(holder.bool){
            imageList[position].setDensity(1);
            if(position!=0)holder.imageView.setImageBitmap(imageThumbList[position]);
            else holder.imageView.setImageBitmap(imageList[position]);
            holder.imageView.setTag("imageTag"+String.valueOf(position));
        }else
        {
            holder.imageView.setVisibility(View.GONE);
            DatabaseHelper db = new DatabaseHelper(context);
            db.removeImage(imagesDBReference[position].getAsInteger("id"));
            File file = new File(new File(imagesDBReference[position].getAsString(DatabaseHelper.IMAGE_FILEPATH_attr)),imagesDBReference[position].getAsString(DatabaseHelper.IMAGE_FILENAME_attr));
            if(file.exists())if(!(file.delete()))Toast.makeText(context,"SomethingWRONG check:\n"+context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return imageList.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        boolean bool;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.pageritem_photo_imageView);
        }
    }

}
