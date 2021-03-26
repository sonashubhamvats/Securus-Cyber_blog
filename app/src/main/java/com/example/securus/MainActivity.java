package com.example.securus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    public void RegisterInLogin(View view)
    {
        startActivity(new Intent(this,Register.class));
    }
    public void onClickLogin(View view)
    {
        EditText email=findViewById(R.id.email);
        EditText password=findViewById(R.id.password);
        String emailS=email.getText().toString();
        String passwordS=password.getText().toString();
        if(TextUtils.isEmpty(emailS)||TextUtils.isEmpty(passwordS))
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error!!")
                    .setMessage("Fill in all the details to login")
                    .setPositiveButton("Ok",null)
                    .show();
        }
        else
        {
            firebaseAuth.signInWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(),UserStartActivity.class));
                    }
                    else
                    {
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("!!!!!!")
                                .setMessage("Invalid login credentials")
                                .setPositiveButton("Yes",null)
                                .show();
                    }
                }
            });
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            finish();
            startActivity(new Intent(getApplicationContext(),UserStartActivity.class));

        }else{
            Toast.makeText(this, "No loggrd in user", Toast.LENGTH_SHORT).show();
        }
    }
}