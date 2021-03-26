package com.example.securus;

import java.util.HashMap;
import java.util.Map;

public class User {

    public String firstName,lastName;
    public String mobileNo,EmailId;


    User()
    {

    }
    User(String fn,String ln,String mn,String ei)
    {
        firstName=fn;
        lastName=ln;
        mobileNo=mn;
        EmailId=ei;

    }
    public HashMap<String, Object> AddDataToUserDataBase()
    {
        HashMap<String,Object> newUser=new HashMap<>();
        newUser.put("firstName",firstName);
        newUser.put("lastName",lastName);
        newUser.put("mobileNo",mobileNo);
        newUser.put("EmailId",EmailId);
        return  newUser;
    }



}
