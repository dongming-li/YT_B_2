package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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

/**
 * This class contains all the necessary functions in order to display the user's items.
 */
public class UserItemsPage extends AppCompatActivity {

    private ListView listView;
    private RequestQueue requestQueue;
    private String token;
    private JSONArray userItemsArray;
    private ArrayList<String> itemsHouseIDArrayList;
    private ArrayList<String> itemIDArrayList;
    private ArrayList<String> items;
    private String userID;
    private JSONObject userInfo;
    private String userIDFromRequest;
    private RestAPI api;
    private final String TAG = UserItemsPage.class.getSimpleName();

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_items);

        requestQueue = Volley.newRequestQueue(this);
        token = (String) getIntent().getSerializableExtra("token");
        Log.d("DEBUG TOKEN IN USER ITEMS PAGE", "token is " + token);
        api = new RestAPI(requestQueue, token);
        items = new ArrayList<>();
        itemsHouseIDArrayList = new ArrayList<>();
        itemIDArrayList = new ArrayList<>();

        getUserID();
        getUserItems();
        Log.d("DEBUG_USER_ITEMS_PAGE", "Made it through user items page");
    }

    /**
     * GET request using the RestAPI to get all the information of the current user
     * Calls the parseUserInfo() method
     */
    private void getUserID()
    {
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
     * Sets the variable userIDFromRequest as the current user's ID
     */
    public void parseUserInfo()
    {
        Log.d("DEBUG_PROFILE", "Made it to parseUserInfo");
        Log.d("DEBUG_PROFILE", userInfo.toString());


        // Log.d("DEBUG_PROFILE", userLastNames.toString());
        try {

            userIDFromRequest = userInfo.getString("sub");
            Log.d("DEBUG_PROFILE_FIRST", "UserID: " + userIDFromRequest.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET request to get list of Items from server
     * Calls the parseItems() method
     */
    private void getUserItems()
    {
        String url = "http://proj-309-yt-b-2.cs.iastate.edu:8080/api/items";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status != 200) {
                        String message = response.getString("message");
                        Log.d("STATUS", "status is: " + status);
                    }
                    else {
                        userItemsArray = response.getJSONArray("data");
                        Log.v("STATUS", "status: " + status);
                        Log.v("ARRAY", userItemsArray.toString());
                        parseItems();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Error response is:" + error.toString());
                System.out.println(error.getMessage());
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
     * Parses the information received from the GET request in the getUserItems() method
     * Sets the variables itemName, itemHouseID, and itemID
     * Calls the displayListView() method
     */
    private void parseItems()
    {
        String itemName;
        String itemID;
        String itemHouseID;
        try {
            Log.d("WHAT THE HECK", "DID I MAKE IT????");
            for (int i = 0; i < userItemsArray.length(); i++) {
                JSONObject eachItem = userItemsArray.getJSONObject(i);
                userID = eachItem.getString("OwnerID");
                // Log.d("USER IDS", "ItemOwnerID: " + userID.toString());
                // Log.d("USER IDS", "UserID: " + userIDFromRequest.toString());
                if(userID.equalsIgnoreCase(userIDFromRequest)) {
                    itemName = eachItem.getString("ItemName");
                    itemHouseID = eachItem.getString("ItemsHouseID");
                    itemID = eachItem.getString("ItemID");
                    itemIDArrayList.add(itemID);
                    items.add(itemName);
                    itemsHouseIDArrayList.add(itemHouseID);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        displayListview();
        Log.d("Items Arraylist", "items: " + items);
    }

    /**
     * Displays the list of the current user's items in the UI
     */
    private void displayListview()
    {
        Log.d("DEBUG", "DISPLAY LISTVIEW");
        if(items == null)
        {
            items.add("No items");
            listView = findViewById(R.id.user_items_list_view);
            Log.d("DEBUG", "WENT INTO NULL IF");
            ArrayAdapter<String> itemNames = new ArrayAdapter<>(this, R.layout.listview4, items);
            listView.setAdapter(itemNames);
        }
        else {
            listView = findViewById(R.id.user_items_list_view);
            Log.d("DEBUG", "WENT INTO ELSE");
            ArrayAdapter<String> itemNames = new ArrayAdapter<>(this, R.layout.listview4, items);
            listView.setAdapter(itemNames);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent;
                String item;
                String house;
                String itemsID;

                switch (position) {
                    case 0:
                        //item 1
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(0);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(0);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(0);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 1:
                        //item 2
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(1);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(1);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(1);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 2:
                        //item 3
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(2);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(2);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(2);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 3:
                        //item 4
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(3);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(3);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(3);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 4:
                        //item 5
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(4);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(4);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(4);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 5:
                        //item 6
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(5);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(5);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(5);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 6:
                        //item 7
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(6);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(6);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(6);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 7:
                        //item 8
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(7);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(7);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(7);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 8:
                        //item 9
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(8);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(8);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(8);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 9:
                        //item 10
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(9);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(9);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(9);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 10:
                        //item 11
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(10);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(10);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(10);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 11:
                        //item 12
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(11);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(11);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(11);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 12:
                        //item 13
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(12);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(12);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(12);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 13:
                        //item 14
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(13);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(13);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(13);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 14:
                        //item 15
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(14);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(14);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(14);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 15:
                        //item 16
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(15);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(15);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(15);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 16:
                        //item 17
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(16);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(16);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(16);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 17:
                        //item 18
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(17);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(17);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(17);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 18:
                        //item 19
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(18);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(18);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(18);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    case 19:
                        //item 20
                        intent = new Intent(UserItemsPage.this, ItemInfoPage.class);
                        //send item name in intent
                        item = items.get(19);
                        intent.putExtra("item", item);
                        // house name in intent
                        house = itemsHouseIDArrayList.get(19);
                        intent.putExtra("house", house);
                        intent.putExtra("token", token);
                        itemsID = itemIDArrayList.get(19);
                        intent.putExtra("itemID", itemsID);
                        startActivity(intent);
                        break;
                    default:
                        Log.d("DEFAULT_CASE_STATEMENT_IN_BORROWED_ITEMS", "Item not handled yet");
                        break;

                }
            }
        });
    }
}
