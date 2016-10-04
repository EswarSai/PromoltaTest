package psai.develop.promoltatest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import psai.develop.promoltatest.BaymackResponse;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private int RC_SIGN_IN = 100;

    private SignInButton signInButton;
    private GoogleSignInOptions options;
    private GoogleApiClient apiClient;

    private TextView nameTV;
    private TextView emailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        nameTV = (TextView) findViewById(R.id.nameTV);
        emailTV = (TextView) findViewById(R.id.emailTV);

        signInButton = (SignInButton) findViewById(R.id.signInBtn);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(options.getScopeArray());
        signInButton.setOnClickListener(this);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if(id == R.id.signInBtn){
            signIn();
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){  // result of getSignInIntent
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInIntent(result);
        }
    }

    private void handleSignInIntent(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            nameTV.setText(account.getDisplayName());
            emailTV.setText(account.getEmail());
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Google login failed", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
    }


}
