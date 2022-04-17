package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class allows the user to sign onto CySwapper using any account that is connected to Google.
 */
public class GoogleSignOn extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, com.android.volley.Request.Method{

    private LinearLayout Prof_Section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView Name, Email;
    private ImageView Prof_Pic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private TextView mTextView;
    private String accesstoken;

    private String TAG = GoogleSignOn.class.getSimpleName();
    //protected RequestQueueHandler requestQueueHandler;
    private JSONArray json_string;
    private JSONObject json_object;
    private ArrayList<String> names;
    private ArrayList<String> availabilities;
    public RequestQueue rq;
    private JSONArray userItemsArray;
    private RestAPI api;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_on);
        SignIn = findViewById(R.id.bn_login);
        Prof_Section = findViewById(R.id.activity_google_sign_on);
        SignIn.setOnClickListener(this);
        Prof_Section.setVisibility(View.VISIBLE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestServerAuthCode("985488744996-1non9kquakbajdmola2hl4hdgnefcc3b.apps.googleusercontent.com")
                .build();
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
        mTextView = findViewById(R.id.text);
        rq = Volley.newRequestQueue(this);
        api = new RestAPI(rq);

        // api = new RestAPI("http://proj-309-yt-b-2.cs.iastate.edu:8080/api", "/authcode");

    }

    /**
     * Calls the signIn() method when the Google sign in button is clicked
     * @param view    The view from the UI that was clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bn_login:
                signIn();
                break;
        }
    }

    /**
     * Called when there was an error connecting the client to the service.
     * @param connectionResult      Used to determine what the connection error was
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Log.d(TAG, "onConnectionFailed" + connectionResult);
    }

    /**
     * Checks for authentication when signing in
     */
    private void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    /**
     * Calls the updateUI() method in order to remove persona information when signing out the user
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    /**
     * Uses RestAPI to login and send authentication code to server
     * Receives token from server
     * @param result           Contains user information
     */
    private void handleResult(GoogleSignInResult result) {
        Log.d(TAG, "Result of Login: " + result.toString());
        Log.d(TAG, "Status code: " + result.getStatus().getStatusCode());
        Log.d(TAG, "Success: " + result.isSuccess());

        if (result.getSignInAccount() != null) {
            GoogleSignInAccount acct = result.getSignInAccount();
            final String authCode = acct.getServerAuthCode();
            Log.d("AUTHCODE", "WHAT IS MY AUTHCODE: " + authCode);

            // api.Login(authCode);

            final Intent intent = new Intent(GoogleSignOn.this, HomePage.class);


            Log.d(TAG, "GoogleSignInAccount getDisplayName: " + acct.getDisplayName());
            Log.d(TAG, "GoogleSignInAccount getServerAuthCode: " + acct.getServerAuthCode());
            Log.d(TAG, "GoogleSignInAccount getIdToken: " + acct.getIdToken());

            // Login to api please
            Log.d(TAG, "Using authCode: " + authCode);
            api.Login(authCode, new LoginCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Login success.");
                    Log.d("DID I LOG IN?????????????", "Login token: " + api.GetToken());

                    intent.putExtra("token", api.GetToken());
                    startActivity(intent);
                    Log.d("DEBUG_GSO_POST_TOKEN?!?!", "TOKEN IS: " + api.GetToken());


                }

                @Override
                public void onFail() {
                    Log.d(TAG, "Login failed.");
                }

                @Override
                public void onToast(String message) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.d(TAG, "getSignInAccount object is null");
        }

        // Log.d("DEBUG_GSO_POST_TOKEN?!?!", "TOKEN IS: " + api.GetToken());


        if(result.isSuccess()) {
            Log.d(TAG, String.format("getSignInAccount object is null: %b", result.getSignInAccount() == null));

            // updateUI(true);

            // intent.putExtra()
        }
        else {
            Log.d(TAG, "FAILED");
            // updateUI(false);
        }
    }

    /**
     * Updates the UI with personal information when signed in and removes sign in button once signed in
     * @param isLogin       Used to determine if the user has signed in
     */
    private void updateUI(boolean isLogin) {
        if(isLogin) {
            // Prof_Section.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
        }
        else {
            // Prof_Section.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * Uses requestCode to authenticate Google sign on
     * Calls the handleResult() method
     * @param requestCode   The integer request code originally supplied to startActivityForResult(),
     *                      allowing you to identify who this result came from
     * @param resultCode    The integer result code returned by the child activity through its setResult()
     * @param data          An Intent, which can return result data to the caller
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("MADE IT HERE");
        if(requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
