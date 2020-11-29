package com.example.bookfanatics.code;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookfanatics.LauncherActivity;
import com.example.bookfanatics.R;
import com.example.bookfanatics.system.Login;

public class LoginRecycler extends RecyclerView.Adapter<LoginRecycler.Holder>{

    Login[] loginList;
    Context context;
    LayoutInflater inflater;
    DatabaseHelper db;

    public LoginRecycler(Context context, Login[] loginList){
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.loginList = loginList;
    this.context = context;
    db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycleritem_login,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.id=loginList[position].getid();
    holder.usernameText.setText(loginList[position].getusername());
        holder.emailText.setText(loginList[position].getemail());
        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.deleteSafe) {
                    DatabaseHelper db2= new DatabaseHelper(context);
                    db2.removeLogin(holder.id);
                    holder.itemView.setVisibility(View.GONE);
                    Toast.makeText(context,context.getString(R.string.login_postdelete_message).replace("%login",holder.usernameText.getText()),Toast.LENGTH_LONG).show();
                    if(StaticEnviroment.UserLogin!=null)if(StaticEnviroment.UserLogin.getid() == holder.id){
                        SharedPreferences.Editor preference = context.getSharedPreferences(LauncherActivity.SAVEPASSWORD_PREFERENCE,0).edit();
                        preference.putBoolean("passwordSaved",false);
                        preference.commit();
                        context.startActivity(new Intent(context, LauncherActivity.class));
                    }
                }
                else {
                    holder.deleteSafe = true;
                    Toast.makeText(context,context.getString(R.string.login_safedelete_message),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return loginList.length;
    }

    class Holder extends  RecyclerView.ViewHolder {
        public TextView usernameText, emailText;
        public View delButton,itemView;
        boolean deleteSafe;
        int id;

        public Holder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.loginitem_text_username);
            emailText = itemView.findViewById(R.id.loginitem_text_email);
            delButton = itemView.findViewById(R.id.loginitem_button_x);
            this.itemView = itemView;
            deleteSafe = false;
        }
    }
}
