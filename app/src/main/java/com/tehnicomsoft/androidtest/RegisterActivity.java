package com.tehnicomsoft.androidtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        final EditText etUsername = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPass);
        Button btnReg = (Button) findViewById(R.id.btnReg);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterActivity.this, ChatActivity.class));
            finish();
        }

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.createUserWithEmailAndPassword(etUsername.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
//                            FirebaseMessaging.getInstance().subscribeToTopic(
//                                    "user_" + etUsername.getText().toString().substring(0, etUsername.getText().toString().indexOf("@")));
                            FirebaseMessaging.getInstance().subscribeToTopic("user_" + "chat");
                            startActivity(new Intent(RegisterActivity.this, ChatActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Not successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
