package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class ItemInfoPage extends AppCompatActivity {

    private TextView itemName;
    private TextView houseName;
    private String item;
    private String house;
    private String itemID;
    private Button delete;
    private RequestQueue requestQueue;
    private String token;
    private JSONObject itemFromRequest;
    private RestAPI restAPI;
    private final String TAG = ItemInfoPage.class.getSimpleName();

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info_page);

        token = (String) getIntent().getSerializableExtra("token");
        System.out.println("TOKEN: " + token);


        requestQueue = Volley.newRequestQueue(this);

        item = (String) getIntent().getSerializableExtra("item");
        house = (String) getIntent().getSerializableExtra("house"); //later write if statement to change the houseID to houseName
        itemID = (String) getIntent().getSerializableExtra("itemID");

        itemName = findViewById(R.id.item_name);
        itemName.setText(item);

        houseName = findViewById(R.id.house_name);
        houseName.setText(house);

        String itemsUrl = "http://proj-309-yt-b-2.cs.iastate.edu:8080/api/items/";

        restAPI = new RestAPI(requestQueue, itemsUrl, null, token);

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRequest();
                //refreshItemsPage();
            }
        });
    }

    /**
     * makes a DELETE request for an item selected based off of itemID
     */
    public void deleteRequest()
    {
        String url = "http://proj-309-yt-b-2.cs.iastate.edu:8080/api/items/";
        url += itemID;
        Log.d("DEBUG_DELETING_URL", url);
        Log.d("DEBUG_BORROWED_ITEMS_DELETE_ R", "Made it into deleteItems()");

        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(ItemInfoPage.this, response.toString(), Toast.LENGTH_LONG).show();
                try {
                    Log.d("DEBUG IN ON RESPONSE FOR DELETE", "Response is: " + response.toString());
                    itemFromRequest = response.getJSONObject("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG_DELETE_ITEM", e.getMessage());
                }

                Log.d("DEBUG_BORROWED_ITEMS_DELETE_ RRRRRRRRRRRRR", "Made it into deleteItems() onResponse()");
                Intent intent = new Intent(ItemInfoPage.this, ProfilePage.class);
                intent.putExtra("token", token);
                //TODO debug the 400 error
                startActivity(intent); //this is giving a 400 error response -> debug this
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_BORROWED_ITEMS_DELETE_ R", "Error: " + error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                Log.d("DEBUG_ITEM_INFO_PAGE_DELETE", "Token: " + token);
                return h;
            }
        };
        requestQueue.add(deleteRequest);
    }
}
