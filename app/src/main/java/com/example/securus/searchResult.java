package com.example.securus;

public class searchResult {

    private String name,email;
    private  int noposts;
    searchResult()
    {

    }
    searchResult(String n,String e,int noposts)
    {
        name=n;
        email=e;
        this.noposts=noposts;
    }
    public String getNamee()
    {
        return name;
    }
    public String getEmail()
    {
        return  email;
    }
    public int getPosts()
    {
        return  noposts;
    }
    public void setNamee(String n)
    {
        name=n;
    }
    public void setEmail(String e)
    {
        email=e;
    }
    public void setNoofPosts(int p)
    {
        noposts=p;
    }








}
