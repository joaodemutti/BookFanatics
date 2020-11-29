package com.example.bookfanatics.code;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfanatics.LibraryActivity;
import com.example.bookfanatics.LibraryListActivity;
import com.example.bookfanatics.R;
import com.example.bookfanatics.system.Library;

public class LibraryRecyclerAdapter extends RecyclerView.Adapter<LibraryRecyclerAdapter.Holder> {
public final static String SEND_LIBRARY="com.example.bookfanatics.LibraryRecyclerView.SEND_LIBRARY";
    LayoutInflater inflater;
    Library[] list;
    Context context;
    double latitude, longitude;
    int length;
    int mode;
    public View visibleProgress;


    public LibraryRecyclerAdapter(Context context, Library[] list, int mode0, int length)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(list.length<=length)
            this.list = list.clone();
        else {
            this.list = new Library[length];
            for (int i = 0; i < length; i++)
                this.list[i] = list[i];
        }
        this.mode = mode0;
        this.context = context;
        this.length = length;

    }


    public LibraryRecyclerAdapter(Context context, Library[] list, double latitude, double longitude, int length) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(list.length<=length)
            this.list = list.clone();
        else {
            this.list = new Library[length];
            for (int i = 0; i < length; i++)
                this.list[i] = list[i];
        }
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.length = length;
        mode = 6;
    }

    @NonNull
    @Override
    public LibraryRecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycleritem_library, parent, false);
        LibraryRecyclerAdapter.Holder holder = new LibraryRecyclerAdapter.Holder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull LibraryRecyclerAdapter.Holder holder, int position) {
        holder.library = list[position];
        holder.name.setText(list[position].name);
        holder.price.setText(list[position].status);
        if(mode==6)holder.distance.setText(String.format("%.2fKm",distanceBettwen(
                latitude,
                longitude,
                list[position].latitude,
                list[position].longitude
                )));
        else holder.distance.setVisibility(View.GONE);

        for(double i = 0; i<=4;i++)
            holder.stars[(int)i].setImageDrawable(context.getDrawable(
                    list[position].rate>=i+1?
                            R.drawable.ic_star_full:
                            list[position].rate>=i+0.5d?
                                    R.drawable.ic_star_half:
                                    R.drawable.ic_star_none));;
    }

    @Override
    public int getItemCount() {
        return list.length;

    }

    class Holder extends RecyclerView.ViewHolder {
        TextView name, distance, price;
        ImageView[] stars;
        Library library;
        View progressView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.findViewById(R.id.libraryitem_progressBar).setVisibility(View.VISIBLE);
                    DatabaseHelper db = new DatabaseHelper(context);
                    Library dbReference = db.findLibrary(library.address);
                    if(dbReference==null)
                    {
                        if(!(db.insertLibrary(library)>0)){
                            Toast.makeText(context,"insertError",Toast.LENGTH_LONG).show();
                            progressView.setVisibility(View.GONE);
                            return;
                        }
                        dbReference = db.findLibrary(library.address);
                        if(dbReference==null){
                            Toast.makeText(context,"insertError",Toast.LENGTH_LONG).show();
                            progressView.setVisibility(View.GONE);
                            return;
                        }
                    }

                    Intent intent = new Intent(context,LibraryActivity.class);
                            intent.putExtra(LibraryListActivity.LIBRARYID_INTENT_EXTRA, dbReference.id);
                    context.startActivity(intent);
                    visibleProgress = progressView;
                }
            });
            name = itemView.findViewById(R.id.libraryitem_name);
            distance = itemView.findViewById(R.id.libraryitem_distance);
            price = itemView.findViewById(R.id.libraryitem_status);
            stars = new ImageView[]{
                    (ImageView) itemView.findViewById(R.id.star0),
                    (ImageView) itemView.findViewById(R.id.star1),
                    (ImageView) itemView.findViewById(R.id.star2),
                    (ImageView) itemView.findViewById(R.id.star3),
                    (ImageView) itemView.findViewById(R.id.star4)
            };
            progressView = itemView.findViewById(R.id.libraryitem_progressBar);

        }
    }

    public Double distanceBettwen(double latitude1, double longitude1, double latitude2, double longitude2)
    {
        latitude1 = Math.toRadians(latitude1);
        longitude1 = Math.toRadians(longitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude2 = Math.toRadians(longitude2);

        double deltaLongitude = longitude2 - longitude1;
        double deltaLatitude = latitude2 - latitude1;
        double a = Math.pow(Math.sin(deltaLatitude / 2), 2)
                + Math.cos(latitude1) * Math.cos(latitude2)
                * Math.pow(Math.sin(deltaLongitude / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double r = 6377;

        return(c * r);
    }



}

