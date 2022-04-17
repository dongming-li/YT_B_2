package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
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

/**
 * Created by Kristina on 10/30/2017.
 */

public class HousesPage extends AppCompatActivity implements com.android.volley.Request.Method, AdapterView.OnItemClickListener{

    private JSONArray itemsArray;
    private RequestQueue requestQueue;
    private ArrayList<String> housesNames;
    private ListView listView;
    private ArrayList<String> items;
    private ArrayList<String> itemHouseIDArray;
    private ArrayList<String> itemIDArray;
    private String dormName;
    private String house;
    private String houseID;
    private String token;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houses_page);

        requestQueue = Volley.newRequestQueue(this);

        housesNames = (ArrayList<String>) getIntent().getStringArrayListExtra("dormHouses");
        dormName = (String) getIntent().getSerializableExtra("dorm");
        token = (String) getIntent().getSerializableExtra("token");
        Log.d("TOKEN BEGINING OF HOUSES", "Token is:" + token);
        Log.d("DORM BEGINING OF HOUSES", "dorm is:" + dormName);

        itemRequest();
        displayListview();
    }

    /**
     * Request items list from server
     */
    private void itemRequest()
    {
        Log.d("DEBUG_HOUSES", "Made it to dormRequest");
        String url = "http://proj-309-yt-b-2.cs.iastate.edu:8080/api/items";
        Log.d("DEBUG_HOUSES", url);


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
                        itemsArray = response.getJSONArray("data");
                        if(itemsArray == null) {
                            Log.v("DEBUG", "item names was null");
                        }
                        Log.v("STATUS", "status: " + status);
                        Log.v("ARRAY", itemsArray.toString());
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
                //Log.d("ERROR_DORMS", error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                Log.d("TOKEN_IN_ITEMS_REQUEST", "token is: " + token);
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        requestQueue.add(jor);
    }

    /**
     * Parses items based on the houseID
     *
     * If item is in the house we want to look at then we add it to the items list
     * else go to next iteration of for loop
     */
    public void parseItems()
    {
        Log.d("DEBUG", "MADE IT TO parseHouses");
        String itemName;
        String itemHouseID;
        String itemID;
        items = new ArrayList<>();
        itemHouseIDArray = new ArrayList<>();
        itemIDArray = new ArrayList<>();

        try {
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject eachItem = itemsArray.getJSONObject(i);
                itemName = eachItem.getString("ItemName");
                itemHouseID = eachItem.getString("ItemsHouseID");
                itemID = eachItem.getString("ItemID");

                if(itemHouseID.equals(houseID))
                {
                    System.out.println(itemName);
                    items.add(itemName);
                    itemHouseIDArray.add(itemHouseID);
                    itemIDArray.add(itemID);
                    Log.d("DEBUG_DORMS", itemName);
                }
            }
            if(items.isEmpty())
            {
                items.add("Empty Items List");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays all of the houses in the dorm the user selected and sets
     * the onclicklistener for the houses to go to the items that are in that house.
     */
    private void displayListview() {
        listView = (ListView) findViewById(R.id.houses_list_view);
        ArrayAdapter<String> houseNames = new ArrayAdapter<String>(this, R.layout.listview2, housesNames);
        listView.setAdapter(houseNames);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent;

                switch (position) {
                    case 0:
                        if (dormName.equals("barton")) {
                            house = "anders";
                            houseID = "1";
                        }
                        if (dormName.equals("roberts")) {
                            house = "fairchild";
                            houseID = "42";
                        }
                        if (dormName.equals("welch")) {
                            house = "ayres";
                            houseID = "45";
                        }
                        if (dormName.equals("willow")) {
                            house = "anderson";
                            houseID = "49";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "doolittle";
                            houseID = "57";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "bean";
                            houseID = "66";
                        }
                        if (dormName.equals("memorialUnion")) {
                            house = "fifth";
                            houseID = "81";
                        }
                        if (dormName.equals("wallace")) {
                            house = "errington";
                            houseID = "83";
                        }
                        if (dormName.equals("wilson")) {
                            house = "gwynne";
                            houseID = "93";
                        }
                        if (dormName.equals("friley")) {
                            house = "anthony";
                            houseID = "103";
                        }
                        if (dormName.equals("eaton")) {
                            house = "davidson";
                            houseID = "127";
                        }
                        if (dormName.equals("birch")) {
                            house = "dana";
                            houseID = "3";
                        }
                        if (dormName.equals("helser")) {
                            house = "brown";
                            houseID = "134";
                        }
                        if (dormName.equals("martin")) {
                            house = "boyd";
                            houseID = "147";
                        }
                        if (dormName.equals("elm")) {
                            house = "mcGlade";
                            houseID = "7";
                        }
                        if (dormName.equals("freeman")) {
                            house = "busse";
                            houseID = "11";
                        }
                        if (dormName.equals("larch")) {
                            house = "caine";
                            houseID = "13";
                        }
                        if (dormName.equals("linden")) {
                            house = "brandt";
                            houseID = "21";
                        }
                        if (dormName.equals("lyon")) {
                            house = "barker";
                            houseID = "28";
                        }
                        if (dormName.equals("maple")) {
                            house = "cranor";
                            houseID = "30";
                        }
                        if (dormName.equals("oak")) {
                            house = "durian";
                            houseID = "38";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 1:
                        if (dormName.equals("barton")) {
                            house = "tappan";
                            houseID = "2";
                        }
                        if (dormName.equals("roberts")) {
                            house = "franklin";
                            houseID = "43";
                        }
                        if (dormName.equals("welch")) {
                            house = "bergman";
                            houseID = "46";
                        }
                        if (dormName.equals("willow")) {
                            house = "arnquist";
                            houseID = "50";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "fuller";
                            houseID = "58";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "berry";
                            houseID = "67";
                        }
                        if (dormName.equals("memorialUnion")) {
                            house = "fourth";
                            houseID = "80";
                        }
                        if (dormName.equals("wallace")) {
                            house = "hartman";
                            houseID = "87";
                        }
                        if (dormName.equals("wilson")) {
                            house = "hewitt";
                            houseID = "94";
                        }
                        if (dormName.equals("friley")) {
                            house = "bennett";
                            houseID = "104";
                        }
                        if (dormName.equals("eaton")) {
                            house = "hughes";
                            houseID = "128";
                        }
                        if (dormName.equals("birch")) {
                            house = "lange";
                            houseID = "4";
                        }
                        if (dormName.equals("helser")) {
                            house = "carpenter";
                            houseID = "135";
                        }
                        if (dormName.equals("martin")) {
                            house = "campbell";
                            houseID = "148";
                        }
                        if (dormName.equals("elm")) {
                            house = "merchant";
                            houseID = "8";
                        }
                        if (dormName.equals("freeman")) {
                            house = "vollmer";
                            houseID = "12";
                        }
                        if (dormName.equals("larch")) {
                            house = "cessna";
                            houseID = "14";
                        }
                        if (dormName.equals("linden")) {
                            house = "devitt";
                            houseID = "22";
                        }
                        if (dormName.equals("lyon")) {
                            house = "harwood";
                            houseID = "29";
                        }
                        if (dormName.equals("maple")) {
                            house = "forbes";
                            houseID = "31";
                        }
                        if (dormName.equals("oak")) {
                            house = "fosmark";
                            houseID = "39";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 2:
                        if (dormName.equals("roberts")) {
                            house = "harriman";
                            houseID = "44";
                        }
                        if (dormName.equals("welch")) {
                            house = "beyer";
                            houseID = "47";
                        }
                        if (dormName.equals("willow")) {
                            house = "bates";
                            houseID = "51";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "macRae";
                            houseID = "59";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "cavazos";
                            houseID = "68";
                        }
                        if (dormName.equals("memorialUnion")) {
                            house = "sixth";
                            houseID = "82";
                        }
                        if (dormName.equals("wallace")) {
                            house = "kilbourne";
                            houseID = "85";
                        }
                        if (dormName.equals("wilson")) {
                            house = "johnson";
                            houseID = "95";
                        }
                        if (dormName.equals("friley")) {
                            house = "chamberlain";
                            houseID = "105";
                        }
                        if (dormName.equals("eaton")) {
                            house = "jones";
                            houseID = "129";
                        }
                        if (dormName.equals("birch")) {
                            house = "lindstrom";
                            houseID = "5";
                        }
                        if (dormName.equals("helser")) {
                            house = "elwood";
                            houseID = "136";
                        }
                        if (dormName.equals("martin")) {
                            house = "coovLace";
                            houseID = "149";
                        }
                        if (dormName.equals("elm")) {
                            house = "miller";
                            houseID = "9";
                        }
                        if (dormName.equals("larch")) {
                            house = "cunningham";
                            houseID = "15";
                        }
                        if (dormName.equals("linden")) {
                            house = "fulmer";
                            houseID = "23";
                        }
                        if (dormName.equals("maple")) {
                            house = "friant";
                            houseID = "32";
                        }
                        if (dormName.equals("oak")) {
                            house = "king";
                            houseID =  "40";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 3:
                        if (dormName.equals("welch")) {
                            house = "cassell";
                            houseID = "48";
                        }
                        if (dormName.equals("willow")) {
                            house = "lancaster";
                            houseID = "52";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "maney";
                            houseID = "60";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "day";
                            houseID = "69";
                        }
                        if (dormName.equals("wallace")) {
                            house = "lancelot";
                            houseID = "86";
                        }
                        if (dormName.equals("wilson")) {
                            house = "lamson";
                            houseID = "96";
                        }
                        if (dormName.equals("friley")) {
                            house = "converse";
                            houseID = "106";
                        }
                        if (dormName.equals("eaton")) {
                            house = "lawther";
                            houseID = "130";
                        }
                        if (dormName.equals("birch")) {
                            house = "stevenson";
                            houseID = "6";
                        }
                        if (dormName.equals("helser")) {
                            house = "firkins";
                            houseID = "137";
                        }
                        if (dormName.equals("martin")) {
                            house = "gilman";
                            houseID = "150";
                        }
                        if (dormName.equals("elm")) {
                            house = "turner";
                            houseID = "10";
                        }
                        if (dormName.equals("larch")) {
                            house = "emerson";
                            houseID = "16";
                        }
                        if (dormName.equals("linden")) {
                            house = "hoxie";
                            houseID = "24";
                        }
                        if (dormName.equals("maple")) {
                            house = "hayden";
                            houseID = "33";
                        }
                        if (dormName.equals("oak")) {
                            house = "sims";
                            houseID = "41";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 4:
                        if (dormName.equals("willow")) {
                            house = "lommen";
                            houseID = "53";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "murray";
                            houseID = "61";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "deloria";
                            houseID = "70";
                        }
                        if (dormName.equals("wallace")) {
                            house = "lantz";
                            houseID = "87";
                        }
                        if (dormName.equals("wilson")) {
                            house = "mashek";
                            houseID = "97";
                        }
                        if (dormName.equals("friley")) {
                            house = "dodds";
                            houseID = "107";
                        }
                        if (dormName.equals("eaton")) {
                            house = "nelson";
                            houseID = "131";
                        }
                        if (dormName.equals("helser")) {
                            house = "fleming";
                            houseID = "138";
                        }
                        if (dormName.equals("martin")) {
                            house = "raymond";
                            houseID = "151";
                        }
                        if (dormName.equals("larch")) {
                            house = "greene";
                            houseID = "17";
                        }
                        if (dormName.equals("linden")) {
                            house = "merrill";
                            houseID = "25";
                        }
                        if (dormName.equals("maple")) {
                            house = "knowles";
                            houseID = "34";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 5:
                        if (dormName.equals("willow")) {
                            house = "platt";
                            houseID = "34";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "otopalik";
                            houseID = "62";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "ebbers";
                            houseID = "71";
                        }
                        if (dormName.equals("wallace")) {
                            house = "mcCowen";
                            houseID = "88";
                        }
                        if (dormName.equals("wilson")) {
                            house = "matterson";
                            houseID = "98";
                        }
                        if (dormName.equals("friley")) {
                            house = "godfrey";
                            houseID = "108";
                        }
                        if (dormName.equals("eaton")) {
                            house = "tilden";
                            houseID = "132";
                        }
                        if (dormName.equals("helser")) {
                            house = "haber";
                            houseID = "139";
                        }
                        if (dormName.equals("martin")) {
                            house = "starbuckNorth";
                            houseID = "152";
                        }
                        if (dormName.equals("larch")) {
                            house = "hanson";
                            houseID = "18";
                        }
                        if (dormName.equals("linden")) {
                            house = "stewart";
                            houseID = "26";
                        }
                        if (dormName.equals("maple")) {
                            house = "shilling";
                            houseID = "35";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 6:
                        if (dormName.equals("willow")) {
                            house = "schaefer";
                            houseID = "55";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "rawson";
                            houseID = "63";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "geddes";
                            houseID = "72";
                        }
                        if (dormName.equals("wallace")) {
                            house = "nielsen";
                            houseID = "89";
                        }
                        if (dormName.equals("wilson")) {
                            house = "owens";
                            houseID = "99";
                        }
                        if (dormName.equals("friley")) {
                            house = "henderson";
                            houseID = "109";
                        }
                        if (dormName.equals("eaton")) {
                            house = "woodrow";
                            houseID = "133";
                        }
                        if (dormName.equals("helser")) {
                            house = "livingston";
                            houseID = "140";
                        }
                        if (dormName.equals("martin")) {
                            house = "starbuckSouth";
                            houseID = "153";
                        }
                        if (dormName.equals("larch")) {
                            house = "kehlenbeck";
                            houseID = "19";
                        }
                        if (dormName.equals("linden")) {
                            house = "sullivan";
                            houseID = "27";
                        }
                        if (dormName.equals("maple")) {
                            house = "walls";
                            houseID = "36";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 7:
                        if (dormName.equals("willow")) {
                            house = "tompkins";
                            houseID = "56";
                        }
                        if (dormName.equals("buchanan")) {
                            house = "schmidt";
                            houseID = "64";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "hoyt";
                            houseID = "73";
                        }
                        if (dormName.equals("wallace")) {
                            house = "nuckolls";
                            houseID = "90";
                        }
                        if (dormName.equals("wilson")) {
                            house = "rothacker";
                            houseID = "100";
                        }
                        if (dormName.equals("friley")) {
                            house = "hutton";
                            houseID = "110";
                        }
                        if (dormName.equals("helser")) {
                            house = "louden";
                            houseID = "141";
                        }
                        if (dormName.equals("larch")) {
                            house = "wolf";
                            houseID = "20";
                        }
                        if (dormName.equals("maple")) {
                            house = "young";
                            houseID = "37";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 8:
                        if (dormName.equals("buchanan")) {
                            house = "vance";
                            houseID = "65";
                        }
                        if (dormName.equals("geoffory")) {
                            house = "kooser";
                            houseID = "74";
                        }
                        if (dormName.equals("wallace")) {
                            house = "petersen";
                            houseID = "91";
                        }
                        if (dormName.equals("wilson")) {
                            house = "webber";
                            houseID = "101";
                        }
                        if (dormName.equals("friley")) {
                            house = "kimball";
                            houseID = "111";
                        }
                        if (dormName.equals("helser")) {
                            house = "macDonald";
                            houseID = "142";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 9:
                        if (dormName.equals("geoffory")) {
                            house = "mack";
                            houseID = "75";
                        }
                        if (dormName.equals("wallace")) {
                            house = "rambo";
                            houseID = "92";
                        }
                        if (dormName.equals("wilson")) {
                            house = "werkman";
                            houseID = "102";
                        }
                        if (dormName.equals("friley")) {
                            house = "knapp";
                            houseID = "112";
                        }
                        if (dormName.equals("helser")) {
                            house = "mortensen";
                            houseID = "143";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 10:
                        if (dormName.equals("geoffory")) {
                            house = "massie";
                            houseID = "76";
                        }
                        if (dormName.equals("friley")) {
                            house = "lincoln";
                            houseID = "113";
                        }
                        if (dormName.equals("helser")) {
                            house = "richey";
                            houseID = "144";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 11:
                        if (dormName.equals("geoffory")) {
                            house = "robinson";
                            houseID = "77";
                        }
                        if (dormName.equals("friley")) {
                            house = "lorchRussell";
                            houseID = "114";
                        }
                        if (dormName.equals("helser")) {
                            house = "rowe";
                            houseID = "145";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 12:
                        if (dormName.equals("geoffory")) {
                            house = "roderuck";
                            houseID = "78";
                        }
                        if (dormName.equals("friley")) {
                            house = "lowe";
                            houseID = "115";
                        }
                        if (dormName.equals("helser")) {
                            house = "sadler";
                            houseID = "146";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 13:
                        if (dormName.equals("geoffory")) {
                            house = "tiffany";
                            houseID = "79";
                        }
                        if (dormName.equals("friley")) {
                            house = "meeker";
                            houseID = "116";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 14:
                        if (dormName.equals("friley")) {
                            house = "murphy";
                            houseID = "117";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 15:
                        if (dormName.equals("friley")) {
                            house = "nilesFoster";
                            houseID = "118";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 16:
                        if (dormName.equals("friley")) {
                            house = "noble";
                            houseID = "119";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 17:
                        if (dormName.equals("friley")) {
                            house = "oBryan";
                            houseID = "120";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 18:
                        if (dormName.equals("friley")) {
                            house = "palmer";
                            houseID = "121";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 19:
                        if (dormName.equals("friley")) {
                            house = "pearson";
                            houseID = "122";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 20:
                        if (dormName.equals("friley")) {
                            house = "pennell";
                            houseID = "123";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 21:
                        if (dormName.equals("friley")) {
                            house = "spinney";
                            houseID = "124";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 22:
                        if (dormName.equals("friley")) {
                            house = "stange";
                            houseID = "125";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        break;
                    case 23:
                        if (dormName.equals("friley")) {
                            house = "stanton";
                            houseID = "126";
                        }
                        parseItems();
                        intent = new Intent(HousesPage.this, ListingHouseItemsPage.class);
                        intent.putStringArrayListExtra("houseItems", items);
                        intent.putStringArrayListExtra("itemsHouseID", itemHouseIDArray);
                        intent.putStringArrayListExtra("houseItemIDs", itemIDArray);
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

    /**
     * need this to use onItemClickListener
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {

    }
}