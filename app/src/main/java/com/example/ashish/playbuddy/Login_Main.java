package com.example.ashish.playbuddy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

public class Login_Main extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener {

    public static GoogleSignInOptions gso;
    public static GoogleSignInClient mGoogleSignInClient;
    public static GoogleSignInAccount account;
    public static SignInButton signInButton;


     public static final int RC_SIGN_IN = 03;

    public static final String TAG = "Sign in";
     void indusLog(String message)
     {
         Log.i("MainActivity",message);
     }

     void indusToast(Context context,String message)
     {
         Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

         indusLog(message);
     }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        try {
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

            account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null)
        {

        }
        else
        {
           // call admin or user;
            updateUI(account);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
           updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {


        if(account == null)
        {
            Toast.makeText(this, "Invalid Request", Toast.LENGTH_SHORT).show();
        }
        else if(account.getEmail().equalsIgnoreCase("ashish18052@iiitd.ac.in")  || account.getEmail().equalsIgnoreCase("sarosh18084@iiitd.ac.in")  ||account.getEmail().equalsIgnoreCase("vikash18086@iiitd.ac.in"))
        {

            //admin part
            Intent nt =  new Intent(this,NavigationDrawer.class);
            nt.putExtra("name",account.getDisplayName());
            nt.putExtra("email",account.getEmail());
            nt.putExtra("dp",account.getPhotoUrl());
            nt.putExtra("level",1);
            startActivity(nt);
            finish();
        }
        else if(account.getEmail().endsWith("@iiitd.ac.in") || account.getEmail().equalsIgnoreCase("ashishjain030495@gmail.com") || account.getEmail().equalsIgnoreCase("vikascsepandey@gmail.com") || account.getEmail().equalsIgnoreCase("saroshhasan14@gmail.com")) {
            //user part
            Intent nt = new Intent(this, NavigationDrawer.class);
            nt.putExtra("name", account.getDisplayName());
            nt.putExtra("email", account.getEmail());
            nt.putExtra("dp", account.getPhotoUrl());
            nt.putExtra("level", 2);
            /*TimeLineDatabase tdb = new TimeLineDatabase();

            for(int i = 0; i < 24;i++)
            {
                TimeLine timeLine1 = new TimeLine();
                TimeLine timeLine2 = new TimeLine();
                timeLine1.setStartTime(i + ":00");
                timeLine1.setEndTime(i + ":30");

                timeLine2.setStartTime(i + ":30");
                timeLine2.setEndTime(i+1 + ":00");
                if(i == 23)
                timeLine2.setEndTime(00 + ":00");
                tdb.write(timeLine1,"timeline");
                tdb.write(timeLine2,"timeline");
            }*/

            startActivity(nt);
            finish();
        }
        else if(!account.getEmail().endsWith("@iiitd.ac.in"))
        {
            Toast.makeText(this, "Invalid email use IIITD email only", Toast.LENGTH_SHORT).show();
            mGoogleSignInClient.revokeAccess();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    int logouttoken = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__main);

        Intent logoutintent = getIntent();
        logouttoken = logoutintent.getIntExtra("logouttoken",0);

        if(logouttoken == 1)
        {
            if(mGoogleSignInClient != null)
            mGoogleSignInClient.revokeAccess();
          //  Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        setGooglePlusButtonText(signInButton,"Login to IIITD PlayBuddy with Google");
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Search all the views inside SignInButton for TextView
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            // if the view is instance of TextView then change the text SignInButton
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "Internet Connectivity Issue", Toast.LENGTH_SHORT).show();
    }
}
