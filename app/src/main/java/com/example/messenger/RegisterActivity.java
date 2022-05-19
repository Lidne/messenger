package com.example.messenger;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import FirebaseModels.User;

public class RegisterActivity extends Activity {
    private final String TAG = "RegisterActivity";
    private final int REGISTER_OK = 30;
    private final int PICK_IMAGE_REQUEST = 40;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    private Uri file;

    private EditText nick;
    private EditText email;
    private EditText password;
    private EditText password_repeat;
    private Button registerBtn;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        nick = findViewById(R.id.nick);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password_register);
        password_repeat = findViewById(R.id.password_repeat);
        registerBtn = findViewById(R.id.register);
    }

    public void register(View target) {
        if (password.getText() == null || password_repeat.getText() == null || email.getText() == null
        || nick.getText() == null) {
            Toast.makeText(RegisterActivity.this, R.string.not_filled,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.getText().toString().equals(password_repeat.getText().toString())) {
            Toast.makeText(RegisterActivity.this, R.string.passwords_doesnt_match,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            final DocumentReference docRef = mFirestore.collection("users").document(user.getUid());
                            docRef.set(new User(email.getText().toString(), nick.getText().toString(), user.getUid()));
                            Intent i = new Intent();
                            i.putExtra("email", email.getText().toString());
                            i.putExtra("password", password.getText().toString());
                            setResult(REGISTER_OK, i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, R.string.fail_register,
                                    Toast.LENGTH_SHORT).show();
                            // update ui
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            file = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                file);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}