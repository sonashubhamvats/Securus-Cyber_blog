package com.example.securus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    String firstNameS,lastNameS,mobileNoS;
    public void RegisterButton()
    {
        Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        EditText emailAddressNew=findViewById(R.id.emailRegister);
        EditText passwordNew=findViewById(R.id.passwordRegister);
        EditText confirmPassword=findViewById(R.id.confirmpasswordRegister);
        EditText firstName=findViewById(R.id.firstNameRegister);
        EditText lastName=findViewById(R.id.lastNameRegister);
        EditText mobileNo=findViewById(R.id.mobileNoRegister);
        firstNameS=firstName.getText().toString();
        lastNameS=lastName.getText().toString();
        mobileNoS=mobileNo.getText().toString();
        String newEmail=emailAddressNew.getText().toString();
        String newPassword=passwordNew.getText().toString();
        String confirmPasswordString=confirmPassword.getText().toString();
        if(TextUtils.isEmpty(firstNameS)||TextUtils.isEmpty(lastNameS)||TextUtils.isEmpty(newEmail)||TextUtils.isEmpty(mobileNoS)||
                TextUtils.isEmpty(newPassword)||TextUtils.isEmpty(confirmPasswordString))
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Fill in all the details to register- ")
                    .setPositiveButton("Ok", null)
                    .show();
        }
        else
        {

            if(confirmPasswordString.compareTo(newPassword)!=0)
            {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Error")
                        .setMessage("The passwords do not match!!")
                        .setPositiveButton("Ok", null)
                        .show();

            }
            else
            {
                if(confirmPassword.length()<7)
                {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("Password should be of atleast 7 characters!!")
                            .setPositiveButton("Ok", null)
                            .show();

                }
                else
                {
                    registerUser(newEmail,newPassword);

                }
            }

        }
    }

    private void registerUser(String newEmail, String newPassword)
    {

        auth.createUserWithEmailAndPassword(newEmail,newPassword).addOnCompleteListener(Register.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null)
                    {
                        Toast.makeText(Register.this, "User here", Toast.LENGTH_SHORT).show();
                        User u=new User(firstNameS,lastNameS,mobileNoS,newEmail);
                        HashMap<String, Object> newUserCreds=u.AddDataToUserDataBase();
                        FirebaseDatabase.getInstance().getReference().child("UserInfo").child(user.getUid()).setValue(newUserCreds);
                        FirebaseDatabase.getInstance().getReference().child("UserListF").child(user.getUid()).child("followers").child("count").setValue(0);
                        FirebaseDatabase.getInstance().getReference().child("UserListF").child(user.getUid()).child("following").child("count").setValue(0);
                        FirebaseDatabase.getInstance().getReference().child("UserPosts").child(user.getUid()).child("count").setValue(0);
                        new AlertDialog.Builder(Register.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Success")
                                .setMessage("User Registered Successfully!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        startActivity(new Intent(getApplicationContext(),UserStartActivity.class));
                                    }
                                })
                                .show();

                    }

                }
                else
                {
                    new AlertDialog.Builder(Register.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("Registration failed "+task.getResult().toString())
                            .setPositiveButton("Ok", null)
                            .show();

                }
            }
        });
    }

    public void BackButton(View view)
    {
        finish();
    }
    Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "here", Toast.LENGTH_SHORT).show();
                RegisterButton();
            }
        });
        auth=FirebaseAuth.getInstance();
    }


}