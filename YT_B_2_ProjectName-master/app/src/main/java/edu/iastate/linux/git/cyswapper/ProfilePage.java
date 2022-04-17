package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class sets up the layout of the Profile Page.
 */
public class ProfilePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private String token;
    private TextView Name;
    private ImageView itemImage;
    private ImageView profileImage;
    private TextView Email;
    private JSONObject userInfo;
    private String userName;
    private String userImageURL;
    private RestAPI api;
    private final String TAG = ProfilePage.class.getSimpleName();
    private RequestQueue requestQueue;
    private Button yourItems;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestQueue = Volley.newRequestQueue(this);

        token = (String) getIntent().getSerializableExtra("token");

        itemImage = findViewById(R.id.add_image);

        Name = findViewById(R.id.user_profile_name);
        profileImage = findViewById(R.id.user_profile_photo);
        // Email = findViewById(R.id.user_profile_email);

        api = new RestAPI(requestQueue, token);

        yourItems = (Button) findViewById(R.id.your_items);
        yourItems.setOnClickListener(this);

        getUserInfo();
    }

    /**
     * Calls the yourItemsPage() method when the Your Items button is clicked
     * @param view    The view from the UI that was clicked
     */
    @Override
    public void onClick(View view) {
        Log.d("DEBUG_PROFILE", "Made it into onClick");
        switch (view.getId())
        {
            case R.id.your_items:
                Log.d("DEBUG_PROFILE", "Pressed Your Items Button");
                yourItemsPage();
                break;
        }
    }

    /**
     * Takes the user from the ProfilePage to UserItemsPage when a button is clicked
     */
    public void yourItemsPage() {
        Intent intent = new Intent(ProfilePage.this, UserItemsPage.class);
        Log.d("DEBUG PROFILE INTENT TO USER ITEMS", "token is " + token);
        intent.putExtra("token", token);
        startActivity(intent);
    }

    /**
     * Called when the activity has detected the user's press of the Navigation Bar to determine
     * whether to open it or close it
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Sets up the Navigation Bar and determines the "go to" location after an
     * item is clicked on the nav bar.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(ProfilePage.this, HomePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(ProfilePage.this, MessagesPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_borrowed_items) {
            Intent intent = new Intent(ProfilePage.this, BorrowedItemsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_dorms) {
            Intent intent = new Intent(ProfilePage.this, DormsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_add_items) {
            Intent intent = new Intent(ProfilePage.this, AddItemPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * GET request using the RestAPI to get all the information of the current user
     * Calls the parseUserInfo() method
     */
    public void getUserInfo() {
        Log.d("DEBUG_PROFILE", "Made it to dormRequest");

        api.GET("/api/loggedin", new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
//                    int status = response.getInt("status");
//                    if (status != 200) {
//                        String message = response.getString("message");
//                        Log.d("DEBUG_PROFILE", message);
//                    } else {
                        userInfo = response.getJSONObject("user");

                        Log.v("DEBUG_PROFILE", "user" + userInfo.toString());
                        parseUserInfo();
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG_PROFILE", e.getMessage());
                }

            }

            @Override
            public void onFailResponse(NetworkResponse response) {
                String data = new String(response.data);
//                Integer status = response.statusCode;

                try {
                    JSONObject jdata = new JSONObject(data);
                    String message = (String) jdata.get("message");
                    Log.e(TAG, "postItem fail message: " + message);

                } catch (JSONException e) {
                    Log.e("GET JSON EXCEPTION", "JSON EXCEPTION" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "postItem had some unknown error");
            }

            @Override
            public void onToast(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Parses the information received from the GET request in the getUserInfo() method
     * Sets userName as the current user's name
     * Sets userImageURL as the current user's profile picture
     * Calls the methods setUserInfo() and setProfileImage()
     */
    public void parseUserInfo()
    {
        Log.d("DEBUG_PROFILE", "Made it to parseUserInfo");
        Log.d("DEBUG_PROFILE", userInfo.toString());


        // Log.d("DEBUG_PROFILE", userLastNames.toString());
        try {

            userName = userInfo.getString("name");
            userImageURL = userInfo.getString("picture");
            Log.d("DEBUG_PROFILE_FIRST", "First names: " + userName.toString());
            Log.d("DEBUG_PROFILE_FIRST", "Picture: " + userImageURL.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setUserInfo();
        setProfileImage();
    }

    /**
     * Displays the current user's email picture as the profile picture in the UI
     * This method is called in parseUserInfo()
     */
    public void setProfileImage()
    {
        Log.d("DEBUG_PROFILE_IMAGE_URL", "URL: " + userImageURL);
        Glide.with(ProfilePage.this)
                .load(userImageURL)
                .fitCenter()
                .into(profileImage);
    }

    /**
     * Displays the name of the current user in the UI
     * This method is called in parseUserInfo()
     */
    public void setUserInfo()
    {
        Log.d("DEBUG_PROFILE_USERNAME", "Name: " + userName);
        Name.setText(userName);
    }


}
