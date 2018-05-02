package com.example.waynage7.CS5542_lab3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private SignInButton Signin;
    private TextView name;
    private TextView email;
    private ImageView prof_pic;
    public static GoogleApiClient googleApiclient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Signin = (SignInButton) findViewById(R.id.bn_loginGoogle);
        Signin.setSize(SignInButton.SIZE_ICON_ONLY);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        googleApiclient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        Signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiclient);
                startActivityForResult(intent, REQ_CODE );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();

            try{
                Intent sendhomepage = new Intent(MainActivity.this,MainActivity_addimage.class);
                String name = account.getDisplayName();
                String email = account.getEmail();
                String dpUrl= " ";

                //email= account.getEmail();
                //dpUrl = account.getPhotoUrl().toString();
                sendhomepage.putExtra("p_name",name);
                sendhomepage.putExtra("p_email",email);
                sendhomepage.putExtra("p_url",dpUrl);
                startActivity(sendhomepage);
            }
            catch (Exception e){
                Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(MainActivity.this,"Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkCredentials(View v)
    {
        EditText usernameCtrl = (EditText)findViewById(R.id.txt_uname);
        EditText passwordCtrl = (EditText) findViewById(R.id.txt_Pwd);
        TextView errorText = (TextView)findViewById(R.id.lbl_Error);
        String userName = usernameCtrl.getText().toString();
        String password = passwordCtrl.getText().toString();

        boolean validationFlag = false;
        //Verify if the username and password are not empty.
        if(!userName.isEmpty() && !password.isEmpty()) {
            if(userName.equals(RegisterActivity.username) && password.equals(RegisterActivity.passwword)) {
                validationFlag = true;
            }
        }
        if(!validationFlag)
        {
            errorText.setVisibility(View.VISIBLE);
        }
        else
        {
            //This code redirects the from login page to the home page.
            Intent redirect = new Intent(MainActivity.this, MainActivity_addimage.class);
            String name, email;
            name = RegisterActivity.username;
            email= RegisterActivity.email;
            redirect.putExtra("p_name",name);
            redirect.putExtra("p_email",email);
            startActivity(redirect);
        }
    }

    public void goToregister(View v){
        //This code redirects the from login page to the Register page.
        Intent redirect = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(redirect);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
