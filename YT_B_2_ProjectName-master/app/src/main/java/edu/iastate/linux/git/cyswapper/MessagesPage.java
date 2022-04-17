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
import android.view.MenuItem;

/**
 * This class is meant to display all messages the user has.
 */
public class MessagesPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String token;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        token = (String) getIntent().getSerializableExtra("token");

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
            Intent intent = new Intent(MessagesPage.this, HomePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MessagesPage.this, ProfilePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_borrowed_items) {
            Intent intent = new Intent(MessagesPage.this, BorrowedItemsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_dorms) {
            Intent intent = new Intent(MessagesPage.this, DormsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_add_items) {
            Intent intent = new Intent(MessagesPage.this, AddItemPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
