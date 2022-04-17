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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DormsPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, com.android.volley.Request.Method, AdapterView.OnItemClickListener {

    private ArrayList<String> dorms;
    private RequestQueue requestQueue;
    private ListView listView;
    private JSONArray dormNames;
    private JSONArray housesNames;

    private String token;

    /*Arraylists to hold each dorms houses*/
    private ArrayList<String> bartonHouses;
    private ArrayList<String> robertsHouses;
    private ArrayList<String> welchHouses;
    private ArrayList<String> willowHouses;
    private ArrayList<String> buchananHouses;
    private ArrayList<String> geoffroyHouses;
    private ArrayList<String> memorialUnionHouses;
    private ArrayList<String> wallaceHouses;
    private ArrayList<String> wilsonHouses;
    private ArrayList<String> frileyHouses;
    private ArrayList<String> eatonHouses;
    private ArrayList<String> birchHouses;
    private ArrayList<String> martinHouses;
    private ArrayList<String> helserHouses;
    private ArrayList<String> elmHouses;
    private ArrayList<String> freemanHouses;
    private ArrayList<String> larchHouses;
    private ArrayList<String> lindenHouses;
    private ArrayList<String> lyonHouses;
    private ArrayList<String> mapleHouses;
    private ArrayList<String> oakHouses;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorms_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestQueue = Volley.newRequestQueue(this);

        displayDorms();
        Log.d("DEBUG", requestQueue.toString());

        token = (String) getIntent().getSerializableExtra("token");

        Log.d("DEBUG_DORM_PAGE_TOKEN", "The token is: " + token);

    }

    /**
     * handles the back button being pressed when
     * a) the drawer is open
     * b) if the drawer isn't open
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
     * Controls the navagation bar
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(DormsPage.this, HomePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(DormsPage.this, ProfilePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(DormsPage.this, MessagesPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_borrowed_items) {
            Intent intent = new Intent(DormsPage.this, BorrowedItemsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_add_items) {
            Intent intent = new Intent(DormsPage.this, AddItemPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    /**
     * calls getDorms method
     */
    public void displayDorms()
    {
        Log.d("DEBUG_DORMS", "Made it to displayDorms");
        getDorms();
    }

    /**
     * makes GET request for the dorms list from the server
     */
    public void getDorms()
    {
        Log.d("DEBUG_DORMS", "Made it to dormRequest");
        String url = "http://proj-309-yt-b-2.cs.iastate.edu:8080/api/dorms";
        Log.d("DEBUG_DORMS", url);


        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("DEBUG_DORMS", "Made it into onResponse");

                try {
                    int status = response.getInt("status");
                    if (status != 200) {
                        String message = response.getString("message");
                        Log.d("DEBUG_DORMS", message);
                    } else {
                        dormNames = response.getJSONArray("data");
//                        if(dormNames == null) {
//                            Log.v("DEBUG", "dorm names was null");
//                        }
//                        Log.v("STATUS", "status: " + status);
//                        Log.v("ARRAY", dormNames.toString());
                        parseDorms();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG_DORMS", e.getMessage());
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
//                Log.d("ERROR_DORMS", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        requestQueue.add(jor);

    }

    /**
     * Parses dorms request to get the dorm names
     */
    public void parseDorms()
    {
        Log.d("DEBUG_DORMS", "Made it to parseDorms");
        Log.d("DEBUG_DORMS", dormNames.toString());
        String dormName;
        dorms = new ArrayList<String>();
        Log.d("DEBUG_DORMS", dorms.toString());
        try {
            for (int i = 0; i < dormNames.length(); i++) {
                JSONObject eachItem = dormNames.getJSONObject(i);
                dormName = eachItem.getString("DormName");
                System.out.println(dormName);
                dorms.add(dormName);
                Log.d("DEBUG_DORMS", dormName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        displayListView();
    }

    /**
     * sets up listview for dorms list and sets onCLickListener to go to houses page from each dorm
     */
    private void displayListView() {
        Log.d("DEBUG_DORMS", "Made it to displayListView");
        listView = findViewById(R.id.dorms_list_view);
        ArrayAdapter<String> dormNames = new ArrayAdapter<String>(this, R.layout.activity_listview, dorms);
        listView.setAdapter(dormNames);
        //listView.setOnClickListener(this);
        housesRequest();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent;
                String dorm;

                System.out.println(listView.getChildAt(0).toString());
                switch(position)
                {
                    case 0:
                        // Log.d("DEBUG", bartonHouses.toString());
                        dorm = "barton";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", bartonHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 1:
                        // Log.d("DEBUG", robertsHouses.toString());
                        dorm = "roberts";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", robertsHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 2:
                        Log.d("DEBUG", welchHouses.toString());
                        dorm = "welch";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", welchHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 3:
                        Log.d("DEBUG", willowHouses.toString());
                        dorm = "willow";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", willowHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 4:
                        Log.d("DEBUG", buchananHouses.toString());
                        dorm = "buchanan";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", buchananHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 5:
                        Log.d("DEBUG", geoffroyHouses.toString());
                        dorm = "geoffroy";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", geoffroyHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 6:
                        Log.d("DEBUG", memorialUnionHouses.toString());
                        dorm = "memorialUnion";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", memorialUnionHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 7:
                        Log.d("DEBUG", wallaceHouses.toString());
                        dorm = "wallace";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", wallaceHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 8:
                        Log.d("DEBUG", wilsonHouses.toString());
                        dorm = "wilson";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", wilsonHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 9:
                        Log.d("DEBUG", frileyHouses.toString());
                        dorm = "friley";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", frileyHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 10:
                        Log.d("DEBUG", eatonHouses.toString());
                        dorm = "eaton";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", eatonHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 11:
                        Log.d("DEBUG", birchHouses.toString());
                        dorm = "birch";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", birchHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 12:
                        Log.d("DEBUG", helserHouses.toString());
                        dorm = "helser";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", helserHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 13:
                        Log.d("DEBUG", martinHouses.toString());
                        dorm = "martin";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", martinHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 14:
                        Log.d("DEBUG", elmHouses.toString());
                        dorm = "elm";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", elmHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 15:
                        Log.d("DEBUG", freemanHouses.toString());
                        dorm = "freeman";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", freemanHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 16:
                        Log.d("DEBUG", larchHouses.toString());
                        dorm = "larch";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", larchHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 17:
                        Log.d("DEBUG", lindenHouses.toString());
                        dorm = "linden";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", lindenHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 18:
                        Log.d("DEBUG", lyonHouses.toString());
                        dorm = "lyon";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", lyonHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 19:
                        Log.d("DEBUG", mapleHouses.toString());
                        dorm = "maple";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", mapleHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 20:
                        Log.d("DEBUG", oakHouses.toString());
                        dorm = "oak";
                        intent = new Intent(DormsPage.this, HousesPage.class);
                        intent.putStringArrayListExtra("dormHouses", oakHouses);
                        intent.putExtra("dorm", dorm);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    default:
                        Log.d("DEBUG", "No valid button pressed");
                        break;
                }
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        //need for AdapterView
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //need for AdapterView
    }

    /**
     * GET request for houses list from the server
     */
    public void housesRequest()
    {
        Log.d("DEBUG_HOUSESREQUEST", "Made it to housesRequest");
        String url = "http://proj-309-yt-b-2.cs.iastate.edu:8080/api/houses";
        Log.d("DEBUG_HOUSESREQUEST", url);

        JsonObjectRequest jor1 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("DEBUG", "GOT TO JSONREQUEST RESPONSE");
                try {
                    int status = response.getInt("status");
                    if (status != 200) {
                        String message = response.getString("message");
                        Log.d("DEBUG_DORMS", message);
                    } else {
                        housesNames = response.getJSONArray("data");
                        if(housesNames == null) {
                            Log.v("DEBUG", "house names was null");
                        }
                        Log.v("STATUS", "status: " + status);
                        Log.v("ARRAY", housesNames.toString());
                        parseHouses();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG_DORMS", e.getMessage());
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("DEBUG", "GOT TO JSONREQUEST ERROR");
                // Log.d("ERROR_DORMS", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        Log.d("DEBUG", "LEAVING HOUSE REQUEST");
        requestQueue.add(jor1);
    }

    /**
     * parses houses list and puts them into their corresponding dorms arraylist
     */
    public void parseHouses()
    {
        Log.d("DEBUG", "MADE IT TO parseHouses");
        String houseName;
        String dormID;
        bartonHouses = new ArrayList<String>();
        robertsHouses = new ArrayList<String>();
        welchHouses = new ArrayList<String>();
        willowHouses = new ArrayList<String>();
        buchananHouses = new ArrayList<String>();
        geoffroyHouses = new ArrayList<String>();
        memorialUnionHouses = new ArrayList<String>();
        wallaceHouses = new ArrayList<String>();
        wilsonHouses = new ArrayList<String>();
        frileyHouses = new ArrayList<String>();
        eatonHouses = new ArrayList<String>();
        birchHouses = new ArrayList<String>();
        helserHouses = new ArrayList<String>();
        martinHouses = new ArrayList<String>();
        elmHouses = new ArrayList<String>();
        freemanHouses = new ArrayList<String>();
        larchHouses = new ArrayList<String>();
        lindenHouses = new ArrayList<String>();
        lyonHouses = new ArrayList<String>();
        mapleHouses = new ArrayList<String>();
        oakHouses = new ArrayList<String>();

        try {
            for (int i = 0; i < housesNames.length(); i++) {
                JSONObject eachItem = housesNames.getJSONObject(i);
                dormID = eachItem.getString("DormHouseID");
                Log.d("DEBUG", dormID.toString());
                if(dormID.equals("1")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    bartonHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("2")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    birchHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("3")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    elmHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("4")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    freemanHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("5")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    larchHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("6")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    lindenHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("7")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    lyonHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("8")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    mapleHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("9")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    oakHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("10")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    robertsHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("11")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    welchHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("12")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    willowHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("13")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    buchananHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("14")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    geoffroyHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("15")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    memorialUnionHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("16")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    wallaceHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("17")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    wilsonHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("18")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    frileyHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("19")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    eatonHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("20")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    helserHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
                if(dormID.equals("21")) {
                    houseName = eachItem.getString("HouseName");
                    System.out.println(houseName);
                    martinHouses.add(houseName);
                    Log.d("DEBUG_DORMS", houseName);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
