package com.example.securus;

public class postInAccount {
    private String topic;
    private String post;

    postInAccount()
    {}
    public void setVars(String topic,String post)
    {
        this.post=post;
        this.topic=topic;
    }
    public String getTopic()
    {
        return  topic;
    }
    public String getPost()
    {
        return  post;
    }



}
