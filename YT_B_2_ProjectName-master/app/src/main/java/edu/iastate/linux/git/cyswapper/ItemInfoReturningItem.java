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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kristina on 12/3/2017.
 */

public class ItemInfoReturningItem extends AppCompatActivity {

    private TextView itemName;
    private TextView houseName;
    private String item;
    private String house;
    private String itemID;
    private Button returnItem;
    private RequestQueue requestQueue;
    private String token;
    private RestAPI api;
    private JSONObject itemFromRequest;
    private String pictureID;
    private String ownerID;
    private final String TAG = ItemInfoReturningItem.class.getSimpleName();

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info_returning_item);

        requestQueue = Volley.newRequestQueue(this);
        token = (String) getIntent().getSerializableExtra("token");
        api = new RestAPI(requestQueue, token);
        item = (String) getIntent().getSerializableExtra("item");
        house = (String) getIntent().getSerializableExtra("house");
        itemID = (String) getIntent().getSerializableExtra("itemID");

        itemName = findViewById(R.id.item_name_1);
        itemName.setText(item);

        houseName = findViewById(R.id.house_name_1);
        houseName.setText(house);

        returnItem = findViewById(R.id.returnItem);
        returnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentItemInfo();
                modifyItemInfoForReturn();
                Intent intent = new Intent(ItemInfoReturningItem.this, ProfilePage.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
    }

    /**
     * Gets current state of item so we can update the item in the next method call (modifyItemInfoForReturn)
     */
    private void getCurrentItemInfo() {
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
     * Makes a patch request
     *
     * Changes borrowerID back to null because item has been returned
     * Item avaiability is back to 1 because item is available again
     * Date Borrowed and Date Due is cleared
     */
    private void modifyItemInfoForReturn() {
        String url;
        RestAPI api = new RestAPI(requestQueue);
        api.SetToken(token);

        // actual data
        JSONObject data = new JSONObject();
        try {
            //data.put("ItemPictureID", pictureID);
            //data.put("ItemName",itemName);
            //data.put("OwnerID", ownerID);
            //data.put("ItemsHouseID", house);
            //data.put("ItemID", itemID);
            data.put("BorrowerID", null);
            data.put("DateBorrowed", null);
            data.put("DateDue", null);
            data.put("Available", true);
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