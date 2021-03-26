package com.example.securus;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Feed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Feed extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Feed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Feed.
     */
    // TODO: Rename and change types and number of parameters
    public static Feed newInstance(String param1, String param2) {
        Feed fragment = new Feed();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    void startrefreshTheFeed()
    {
        FirebaseUser u=FirebaseAuth.getInstance().getCurrentUser();
        if(u!=null)
        {
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UserListF").child(u.getUid()).child("following").child("account");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postFeedClassArrayList.clear();
                    tempemailList.clear();
                    tempNameList.clear();
                    tempListUserId.clear();


                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                            tempListUserId.add(dataSnapshot.getValue().toString());
                    }
                    getNameAndEmailId(tempListUserId,0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    void getNameAndEmailId(ArrayList<String> userid,int index)
    {
        if(userid.size()>0)
        {
            FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userid.get(index)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        User u=task.getResult().getValue(User.class);

                        HashMap<String,Object> hashMap=u.AddDataToUserDataBase();

                        tempNameList.add(hashMap.get("firstName").toString()+" "+hashMap.get("lastName").toString());
                        tempemailList.add(hashMap.get("EmailId").toString());
                        if(index==(userid.size()-1))
                        {
                            getPostAndTopic(userid,0);
                        }
                        else
                        {
                            getNameAndEmailId(userid,(index+1));
                        }
                    }
                }
            });

        }
        else
        {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("!!!!!!")
                    .setMessage("Follow people to see posts on your feed!!")
                    .setPositiveButton("Yes",null)
                    .show();
        }
    }
    void getPostAndTopic(ArrayList<String> userid,int index)
    {
        FirebaseDatabase.getInstance().getReference().child("UserPosts").child(userid.get(index)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.getKey().compareToIgnoreCase("count")!=0)
                    {
                        MessageGrp u=dataSnapshot.getValue(MessageGrp.class);
                        temptemppost.add(u.post);
                        temptemptopic.add(u.topic);
                    }

                }

                for(int i=0;i<temptemppost.size();i++)
                {
                    postFeedClass p=new postFeedClass();

                    p.setAll(tempNameList.get(index),temptemptopic.get(i),tempemailList.get(index),temptemppost.get(i));
                    postFeedClassArrayList.add(p);

                }

                temptemppost.clear();
                temptemptopic.clear();
                if(index==(userid.size()-1))
                {
                    try {
                        adaptorFeedPost=new myAdaptorFeedPost(getActivity(),postFeedClassArrayList);
                        lv.setAdapter(adaptorFeedPost);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    getPostAndTopic(userid,(index+1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    ArrayList<String> temptemppost=new ArrayList<String>();
    ArrayList<String> temptemptopic=new ArrayList<String>();

    ArrayList<postFeedClass> postFeedClassArrayList=new ArrayList<postFeedClass>();
    ArrayList<String> tempemailList=new ArrayList<String>();
    ArrayList<String> tempNameList=new ArrayList<String>();
    ArrayList<String> tempListUserId=new ArrayList<String>();
    ListView lv;
    Button refresh;
    myAdaptorFeedPost adaptorFeedPost;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Feed");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        FirebaseUser u= FirebaseAuth.getInstance().getCurrentUser();
        if(u!=null)
        {
            Account.curr_userId=u.getUid();
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lv=getView().findViewById(R.id.postFeedListVIew);
        refresh=getView().findViewById(R.id.refreshFeed);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startrefreshTheFeed();
            }
        });
        startrefreshTheFeed();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }
    class myAdaptorFeedPost extends ArrayAdapter<postFeedClass>
    {

        myAdaptorFeedPost(Context c, ArrayList<postFeedClass> n)
        {
            super(c,0,n);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.post_feed_layout, parent, false);
            }
            postFeedClass s=getItem(position);
            TextView topic=view.findViewById(R.id.topicFeed);
            TextView post=view.findViewById(R.id.postFeed);
            TextView name=view.findViewById(R.id.emailFeed);
            post.setMovementMethod(new ScrollingMovementMethod());
            topic.setText(s.getTopicc());
            post.setText(s.getPostt());
            String t=s.getNamee()+" - "+s.getEmailId();
            name.setText(t);
            return view;
        }
    }
}