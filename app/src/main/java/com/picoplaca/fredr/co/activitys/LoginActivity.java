package com.picoplaca.fredr.co.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.picoplaca.fredr.co.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    ;
    private TextInputLayout TextEmail;
    private TextInputLayout TextPassword;
    private Button btnRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPref = getSharedPreferences("shared", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("login", false)){
            Intent intencion = new Intent(getApplication(), MainActivity.class);
            startActivity(intencion);
            finish();
        }

        iniComponents();
        btnRegister.setOnClickListener(this);

    }

    private void iniComponents() {
        TextEmail = (TextInputLayout) findViewById(R.id.edtuser);
        TextPassword = (TextInputLayout) findViewById(R.id.edtpass);
        btnRegister = (Button) findViewById(R.id.btnlogin);
        progressDialog = new ProgressDialog(this);
    }

    private void registerUser() {
        String email = TextEmail.getEditText().getText().toString().trim();
        String password = TextPassword.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.addemail, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.password, Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage(getApplicationContext().getString(R.string.checktheinfo));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Usuario registrado : " + TextEmail.getEditText().getText(), Toast.LENGTH_LONG).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, R.string.userexist, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.usernoregister, Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void loginUser() {

        final String email = TextEmail.getEditText().getText().toString().trim();
        String password = TextPassword.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.addemail, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.addpassword, Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage(getApplicationContext().getString(R.string.checktheinfo));
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {

                            SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("shared", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("login", true);
                            editor.apply();

                            int pos = email.indexOf("@");
                            String user = email.substring(0, pos);
                            Toast.makeText(LoginActivity.this, "Bienvenido: " + TextEmail.getEditText().getText(), Toast.LENGTH_LONG).show();
                            Intent intencion = new Intent(getApplication(), MainActivity.class);
                            startActivity(intencion);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, R.string.userexist, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.usernoregister, Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                registerUser();
                break;
            case R.id.btnlogin:
                loginUser();
                break;
        }
    }

}
