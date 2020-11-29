package com.example.bookfanatics.system;

import java.io.Serializable;

public class Login implements Serializable {
    private int id;
    private String  username,
                    email,
                    password;

    public Login(){}

    public Login(int id, String username, String email, String password)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Login(String username, String email, String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public boolean validatePassword(String password)
    {
        if(password != null&&this.password!=null)
            return (password!="" && this.password.equals(password));
        else
            return false;
    }

    public void setpassword(String password)
    {
        this.password = password;
    }

    public void setid(int id)
    {
        this.id = id;
    }

    public int getid()
    {
        return id;
    }

    public void setusername(String username)
    {
        this.username = username;
    }

    public String getusername()
    {
        return username;
    }

    public void setemail(String email)
    {
        this.email = email;
    }

    public  String getemail()
    {
        return email;
    }
}

