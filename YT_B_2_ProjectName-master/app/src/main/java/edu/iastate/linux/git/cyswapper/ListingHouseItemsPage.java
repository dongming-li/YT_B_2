package edu.iastate.linux.git.cyswapper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.android.volley.RequestQueue;

import java.util.ArrayList;

/**
 * Created by Kristina on 11/4/2017.
 */

public class ListingHouseItemsPage extends AppCompatActivity implements com.android.volley.Request.Method
{
    private RequestQueue requestQueue;
    private ArrayList<String> itemsNames;
    private ListView listView;
    private ArrayList<String> items;
    private ArrayList<String> itemsHouseIDArrayList;
    private ArrayList<String> itemIDArrayList;
    private String token;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_houses_items_page);

        //requestQueue = Volley.newRequestQueue(this);

        items = (ArrayList<String>) getIntent().getStringArrayListExtra("houseItems");
        itemsHouseIDArrayList = (ArrayList<String>) getIntent().getStringArrayListExtra("itemsHouseID");
        itemIDArrayList = (ArrayList<String>) getIntent().getStringArrayListExtra("houseItemIDs");
        token = (String) getIntent().getSerializableExtra("token");
        displayListview();
    }


    /**
     * sets up listview that displays all of the items in the house that the user selected
     * Also sets up the onItemClicklistener that takes the user to the borrowing item page
     */
    private void displayListview()
    {
        listView = (ListView) findViewById(R.id.houses_items_list_view);
        ArrayAdapter<String> itemNames = new ArrayAdapter<String>(this, R.layout.listview3, items);
        listView.setAdapter(itemNames);

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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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
                        intent = new Intent(ListingHouseItemsPage.this, ItemInfoBorrowingItem.class);
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