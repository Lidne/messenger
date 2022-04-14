package com.example.messenger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class AuthActivity extends Activity {
    private final int REGISTER_REQUEST = 30;
    private final String TAG = "AuthActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    private Dialog dialog;
    private EditText email;
    private EditText password;
    private Button logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        dialog = new Dialog(AuthActivity.this);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        logInBtn = findViewById(R.id.logIn);
    }

    public void startRegisterActivity(View target) {
        Intent i = new Intent(AuthActivity.this, RegisterActivity.class);
        startActivityForResult(i, REGISTER_REQUEST);
    }

    public void logIn(View target) {
        if (email.getText() == null || password.getText() == null || email.getText().toString().equals("") || password.getText().toString().equals("")) {
            Toast.makeText(AuthActivity.this, R.string.not_filled,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent i = new Intent();

                            setResult(RESULT_OK, i);
                            finish();
                        } else if (!task.isSuccessful() && task.isComplete()) {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, R.string.fail_register,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, R.string.login_fail,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REGISTER_REQUEST:
                email.setText(data.getStringExtra("email"));
                password.setText(data.getStringExtra("password"));
        }
    }
}