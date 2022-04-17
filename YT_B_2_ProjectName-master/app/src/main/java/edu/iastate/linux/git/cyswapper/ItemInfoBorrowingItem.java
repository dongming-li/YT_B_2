package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kristina on 12/3/2017.
 */

public class ItemInfoBorrowingItem extends AppCompatActivity {

    private TextView itemName;
    private TextView houseName;
    private String item;
    private String house;
    private String itemID;
    private String ownerID;
    private String pictureID;
    private Button borrowItem;
    private RequestQueue requestQueue;
    private String token;
    private JSONObject itemFromRequest;
    private JSONObject userInfo;
    private String userIDFromRequest;
    private RestAPI api;
    private Calendar currentDate;
    private int day, month, year;
    private String dateBorrowed, dueDate;
    private final String TAG = ItemInfoBorrowingItem.class.getSimpleName();

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info_borrowing_item);

        calendarForDates();

        requestQueue = Volley.newRequestQueue(this);
        token = (String) getIntent().getSerializableExtra("token");
        api = new RestAPI(requestQueue, token);
        item = (String) getIntent().getSerializableExtra("item");
        house = (String) getIntent().getSerializableExtra("house");
        itemID = (String) getIntent().getSerializableExtra("itemID");

        itemName = findViewById(R.id.item_name_2);
        itemName.setText(item);

        houseName = findViewById(R.id.house_name_2);
        houseName.setText(house);

        borrowItem = findViewById(R.id.borrow);
        borrowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserID();
                getCurrentItemInfo();
                try {
                    modifyItemInfoForBorrow();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ItemInfoBorrowingItem.this, BorrowedItemsPage.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
    }

    /**
     * Request to get information about the user that is signed in using
     * RESTAPI class
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
     * Parses user info request for the user's ID
     */
    public void parseUserInfo()
    {
        Log.d("DEBUG_PROFILE", "Made it to parseUserInfo");
        Log.d("DEBUG_PROFILE", userInfo.toString());


        // Log.d("DEBUG_PROFILE", userLastNames.toString());
        try {

            userIDFromRequest = userInfo.getString("sub");
            Log.d("DEBUG_BORROWED_ITEMS", "UserID: " + userIDFromRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Requests data about a certain item based on its item ID
     */
    private void getCurrentItemInfo() {
        Log.d("DEBUG BORROWING PROCESS", "itemID" + itemID);
        String url = "http://proj-309-yt-b-2.cs.iastate.edu:8080/api/items/" + itemID;
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status != 200) {
                        String message = response.getString("message");
                    }
                    else {
                        itemFromRequest = response.getJSONObject("data");
                        Log.v("STATUS", "status: " + status);
                        Log.v("OBJECT", itemFromRequest.toString());
                        parseItems();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
     * parses item to get pictureID and ownerID of the item requested
     *
     * @throws JSONException
     */
    private void parseItems() throws JSONException {
        pictureID = itemFromRequest.getString("ItemPictureID");
        ownerID = itemFromRequest.getString("OwnerID");
    }

    /**
     * Converts date in format YYYY/MM/DD into epoch time
     *
     * @param date date to be converted
     * @return date in epoch
     *
     * @throws ParseException
     */
    private long getEpochFromDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime() / 1000L;
    }

    /**
     * Method to get information from calendar date picker and convert it into date
     * format YYYY/MM/DD
     */
    private void calendarForDates() {
        currentDate = Calendar.getInstance();

        day = currentDate.get(Calendar.DAY_OF_MONTH);
        month = currentDate.get(Calendar.MONTH);
        year = currentDate.get(Calendar.YEAR);

        dateBorrowed = year + "-" + month + "-" + day;
        Log.d("ALMOST DONE WITH THIS", "Date Borrowed: " + dateBorrowed);
        dueDate = year + "-" + month + "-" + (day + 5);
        Log.d("ALMOST DONE WITH THIS", "Date Due: " + dueDate);

    }

    /**
     * This method creates the JSONObject for the parse request with the data we
     * want to modify and then makes the patch request
     * @throws ParseException
     */
    private void modifyItemInfoForBorrow() throws ParseException {
        String url;
        RestAPI api = new RestAPI(requestQueue);
        api.SetToken(token);

        // actual data
        JSONObject data = new JSONObject();
        try {
            data.put("BorrowerID", userIDFromRequest);
            data.put("DateBorrowed", getEpochFromDate(dateBorrowed)); //change
            data.put("DateDue", getEpochFromDate(dueDate)); //change
            data.put("Available", false);
            Log.d("PATCH ITEM JSON OBJECT", "Item PATCH request object: " + data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("DEBUG RETURNING ITEM", "itemID is " + itemID);
        url = "/api/items/" + itemID;

        api.PATCH(url, data, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject json) {
                // status, message

                try {
                    Integer status = (Integer) json.get("status");
                    String message = (String) json.get("message");

                    Log.d(TAG, "patchItem success message: " + message);

                    // toast the message

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailResponse(NetworkResponse response) {
                String data = new String(response.data);
                Integer status = response.statusCode;

                try {
                    JSONObject jdata = new JSONObject(data);
                    String message = (String) jdata.get("message");
                    Log.e(TAG, "patchItem fail message: " + message);

                } catch (JSONException e) {
                    Log.e(TAG, "patchItem fail data: " + data);
                    //e.printStackTrace();
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
}