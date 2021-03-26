package com.example.securus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class Addpost extends AppCompatActivity {

    Button submitButton;
    EditText topic,post,link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);
        topic=findViewById(R.id.topicPostAdd);
        post=findViewById(R.id.postAdd);
        link=findViewById(R.id.linkAdd);
        submitButton=findViewById(R.id.submitPostButtonAdd);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitButton();
            }
        });
    }
    void SubmitButton()
    {
        String postS=post.getText().toString();
        String topicS=topic.getText().toString();
        String linkS=link.getText().toString();
        if(TextUtils.isEmpty(postS)||TextUtils.isEmpty(topicS))
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error!!")
                    .setMessage("Enter all the details")
                    .setPositiveButton("Yes",null)
                    .show();
        }
        else
        {
            if(linkS.compareToIgnoreCase("Null")==0)
            {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Error!!")
                        .setMessage("Invalid link")
                        .setPositiveButton("Yes",null)
                        .show();

            }
            else
            {
                String uid;
                FirebaseUser u= FirebaseAuth.getInstance().getCurrentUser();
                if(u!=null)
                {
                    uid=u.getUid();
                    FirebaseDatabase.getInstance().getReference().child("UserPosts").child(uid).child("count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                int count=(int)Double.parseDouble(task.getResult().getValue().toString());
                                count++;
                                FirebaseDatabase.getInstance().getReference().child("UserPosts").child(uid).child("count").setValue(count);
                                MessageGrp m=new MessageGrp(linkS,postS,topicS,0);

                                FirebaseDatabase.getInstance().getReference().child("UserPosts").child(uid).child("m"+count).setValue(m.getMessage());
                            }
                        }
                    });
                    finish();
                }
            }
        }


        //startActivity(new Intent(this,UserStartActivity.class));
    }

}