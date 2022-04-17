package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/**
 * The home screen where users can view their notifications and suggested items
 * Users can access their profile and the dorms on campus through this page
 */
public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String token;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_home);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            token = (String) getIntent().getSerializableExtra("token");

            Log.d("DEBUG_HOME_PAGE_TOKEN", "The token is: " + token);


    }

    /**
     * Called when the activity has detected the user's press of the Navigation Bar to determine
     * whether to open it or close it
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu
     * @param menu      The options menu in which you place your items
     * @return          Must return true for the menu to be displayed
     *                  if false, it will not be shown
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected
     * @param item      The menu item that was selected
     * @return          Returns false to allow normal menu processing to proceed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.account_settings) {
            startAccountSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Goes from the home page to the account settings page when the account settings button is pressed
     */
    private void startAccountSettings()
    {
        Intent intent = new Intent(HomePage.this, AccountSettings.class);
        HomePage.this.startActivity(intent);
    }

    /**
     * Sets up the Navigation Bar and determines the "go to" location after an
     * item is clicked on the nav bar.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomePage.this, ProfilePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(HomePage.this, MessagesPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_borrowed_items) {
            Intent intent = new Intent(HomePage.this, BorrowedItemsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_dorms) {
            Intent intent = new Intent(HomePage.this, DormsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_add_items) {
            Intent intent = new Intent(HomePage.this, AddItemPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
