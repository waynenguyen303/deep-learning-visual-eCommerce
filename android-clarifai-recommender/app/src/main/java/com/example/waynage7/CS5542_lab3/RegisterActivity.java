package com.example.waynage7.CS5542_lab3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText r_fullname, r_email, r_username, r_password;
    public static String fullname,email, username,passwword;
    Button regbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        r_fullname = (EditText) findViewById(R.id.txt_userfullname);
        r_email = (EditText) findViewById(R.id.txt_useremail);
        r_username = (EditText) findViewById(R.id.txt_username);
        r_password = (EditText) findViewById(R.id.txt_userpw);
        regbtn = (Button) findViewById(R.id.lbl_Registerbtn);
        regbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                register();
            }
        });
    }

    public void register(){
        initialize();
        if(!validate()){
            Toast.makeText(this,"SignUp has Failed",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Registration Successful, Please Login",Toast.LENGTH_SHORT).show();
            onRegisterSuccess();
        }
    }
    public void onRegisterSuccess(){
        //This code redirects the from login page to the Register page.
        Intent redirect = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(redirect);
    }
    public boolean validate(){
        boolean valid = true;
        if(fullname.isEmpty()||fullname.length()>32){
            r_fullname.setError("Please Enter Valid Full Name");
            valid= false;
        }
        if(email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            r_email.setError("Please Enter Valid Email");
            valid= false;
        }
        if(username.isEmpty()|| username.length()>32){
            r_username.setError("Please Enter Valid Username");
            valid=false;
        }
        if(passwword.isEmpty()){
            r_password.setError("Please Enter Valid Password");
            valid=false;
        }
        return valid;
    }

    public void initialize(){
        fullname = r_fullname.getText().toString().trim();
        email = r_email.getText().toString().trim();
        username = r_username.getText().toString().trim();
        passwword = r_password.getText().toString().trim();
    }

}
