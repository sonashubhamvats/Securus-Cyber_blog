package com.example.securus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Account#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Account extends Fragment {

    public static String curr_userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Account() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Account.
     */
    // TODO: Rename and change types and number of parameters
    public static Account newInstance(String param1, String param2) {
        Account fragment = new Account();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Account");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    TextView followerCount,postCount,followingCount,nameAccount;
    Button addPost,followButton;
    ListView postsAccount;
    ArrayList<postInAccount> postInAccounts=new ArrayList<postInAccount>();
    myAdaptorAccountPost adaptorAccountPost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameAccount=getView().findViewById(R.id.nameAccount);
        followerCount=getView().findViewById(R.id.followerNumberAccount);
        followingCount=getView().findViewById(R.id.followingNumberAccount);
        postCount=getView().findViewById(R.id.postNumberAccount);
        addPost=getView().findViewById(R.id.addpostAccountbutton);
        followButton=getView().findViewById(R.id.followButton);
        postsAccount=getView().findViewById(R.id.postsAccount);
        FirebaseDatabase.getInstance().getReference().child("UserInfo").child(curr_userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    User u=task.getResult().getValue(User.class);
                    nameAccount.setText(u.firstName+" "+u.lastName);

                }
            }
        });

        //starting the page credentials
        FirebaseDatabase.getInstance().getReference().child("UserPosts").child(curr_userId).child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    postCount.setText(task.getResult().getValue().toString());
                }
            }
        });
        FirebaseDatabase.getInstance().getReference().child("UserListF").child(curr_userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    followerCount.setText(task.getResult().child("followers").child("count").getValue().toString());
                    followingCount.setText(task.getResult().child("following").child("count").getValue().toString());
                }
            }
        });
        FirebaseUser u=FirebaseAuth.getInstance().getCurrentUser();
        if(u!=null)
        {
            if(u.getUid().compareTo(curr_userId)==0)
            {
                followButton.setText("Edit Profile");

            }
            else
            {
                addPost.setVisibility(View.INVISIBLE);
                //here one more function needed which will set the follow following button
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UserListF").child(u.getUid()).child("following").child("account");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int flag=0;
                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                        {
                            if(dataSnapshot.getValue().toString().compareTo(curr_userId)==0)
                            {
                                flag=1;
                                break;
                            }
                        }
                        if(flag==1)
                        {
                            followButton.setText("Following");
                            followButton.setBackgroundColor(Color.BLUE);
                        }
                        else
                        {
                            followButton.setText("Follow");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
        FirebaseDatabase.getInstance().getReference().child("UserPosts").child(curr_userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postInAccounts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    if(dataSnapshot.getKey().matches("^m.*"))
                    {
                        MessageGrp m=dataSnapshot.getValue(MessageGrp.class);
                        if(m!=null)
                        {
                            postInAccount p=new postInAccount();
                            p.setVars(m.getTopic(),m.getPost());
                            postInAccounts.add(p);
                        }

                    }
                }
                try {
                    adaptorAccountPost=new myAdaptorAccountPost(getActivity(),postInAccounts);
                    postsAccount.setAdapter(adaptorAccountPost);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //setting the credentials ends here

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowTheUser(u.getUid(),curr_userId);
            }
        });
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddPost();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    void FollowTheUser(String userId,String personId)
    {
        if(followButton.getText().toString().compareTo("Follow")==0)
        {
            FirebaseDatabase.getInstance().getReference().child("UserListF").child(userId).child("following").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            int initialcount=(int)Double.parseDouble(task.getResult().getValue().toString());
                            initialcount++;
                            FirebaseDatabase.getInstance().getReference().child("UserListF").child(userId).child("following").child("count").setValue(initialcount);
                            FirebaseDatabase.getInstance().getReference().child("UserListF").child(userId).child("following").child("account").push().setValue(personId);
                        }
                }
            });
            FirebaseDatabase.getInstance().getReference().child("UserListF").child(personId).child("followers").child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        int initialcount=(int)Double.parseDouble(task.getResult().getValue().toString());
                        initialcount++;
                        FirebaseDatabase.getInstance().getReference().child("UserListF").child(personId).child("followers").child("count").setValue(initialcount);
                        FirebaseDatabase.getInstance().getReference().child("UserListF").child(personId).child("followers").child("account").push().setValue(userId);
                        followButton.setBackgroundColor(Color.BLUE);
                        followButton.setText("Following");
                    }
                }
            });

        }
        else
        {

        }
        //gotta code the unfollow button here par time hi nhi hai lol

    }
    class myAdaptorAccountPost extends ArrayAdapter<postInAccount>
    {

        myAdaptorAccountPost(Context c, ArrayList<postInAccount> n)
        {
            super(c,0,n);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.account_show_posts, parent, false);
            }
            postInAccount s=getItem(position);
            TextView topic=view.findViewById(R.id.topicAccountPost);
            TextView post=view.findViewById(R.id.postContentPost);
            post.setMovementMethod(new ScrollingMovementMethod());
            topic.setText(s.getTopic());
            post.setText(s.getPost());
            return view;
        }
    }
    void onClickAddPost()
    {

        startActivity(new Intent(getActivity(),Addpost.class));
    }


}