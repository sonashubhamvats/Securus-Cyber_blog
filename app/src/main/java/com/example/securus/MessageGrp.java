package com.example.securus;

import java.util.HashMap;

public class MessageGrp {
    String link,post,topic;
    int like;
    MessageGrp()
    {
    }
    MessageGrp(String l,String p,String t,int li)
    {
        link=l;
        post=p;
        topic=t;
        like=li;
    }
    public String getLink()
    {
        return  link;
    }
    public String getPost()
    {
        return  post;
    }
    public String getTopic()
    {
        return  topic;
    }
    public int getLike()
    {
        return  like;
    }




    public HashMap<String,Object> getMessage()
    {
        HashMap<String,Object> messageGrp=new HashMap<>();
        messageGrp.put("link",link);
        messageGrp.put("post",post);
        messageGrp.put("topic",topic);
        messageGrp.put("like",like);
        return messageGrp;
    }




}
