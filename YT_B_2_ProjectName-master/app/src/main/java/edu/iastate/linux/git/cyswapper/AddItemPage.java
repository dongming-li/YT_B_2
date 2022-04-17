package edu.iastate.linux.git.cyswapper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.android.volley.toolbox.Volley;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class contains all the necessary functions that are needed when trying to add an item to CySwapper.
 */
public class AddItemPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String token;
    private EditText itemName;
    private EditText dueDate;
    private EditText houseID;
    private EditText borrowerID;
    private EditText borrowedDate;
    private Button addItem;
    private Calendar mCurrentDate;
    private ImageView itemImage;
    private Button cameraButton;
    private String userIDFromRequest;
    private JSONObject userInfo;
    private RequestQueue rq;
    private int day, month, year;
    private RestAPI api;

    // Tag for log statements
    private final String TAG = AddItemPage.class.getSimpleName();

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_page);

        rq = Volley.newRequestQueue(this);
        token = (String) getIntent().getSerializableExtra("token");
        api = new RestAPI(rq, token);

        findViewByIDs();
        cameraButtonFunctionality();
        drawerSetUp();
        setDueDate();
        setBorrowedDate();
        addItems();
    }

    /**
     * Checks whether the fields on the addItemPage Activity are empty or not
     * Calls the postItem() method to add the itemName, houseID, borrowerID, borrowerDate, and dueDate to the database
     */
    public void addItems() {

            addItem = findViewById(R.id.add);
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO check if string is null
                    if (itemName.getText().toString().trim().length() != 0 &&
                            houseID.getText().toString().trim().length() != 0 && borrowerID.getText().toString().trim().length() != 0 &&
                            borrowedDate.getText().toString().trim().length() != 0 && dueDate.getText().toString().trim().length() != 0) {

                                 postItem(itemName.getText().toString(), houseID.getText().toString(),
                                    borrowerID.getText().toString(), borrowedDate.getText().toString(),  dueDate.getText().toString(), true);
                                 Toast.makeText(AddItemPage.this, "Item was Added", Toast.LENGTH_LONG);
                                 Intent intent = new Intent(AddItemPage.this, ProfilePage.class);
                                 intent.putExtra("token", token);
                                 startActivity(intent);
                    } else {
                        Log.d("ADD ITEMS???????????????", "EMPTY SECTIONS");
                        Toast.makeText(AddItemPage.this, "Empty Sections. Could not add Item. Try again!", Toast.LENGTH_LONG);
                        Intent intent = new Intent(AddItemPage.this, ProfilePage.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    }
                }
            });
    }

    /**
     * Initializes all the variables in the UI by finding the view that was identified by the android:id XML feature
     * Interacts with the widgets in the UI to retrieve cursors for the data being displayed
     * This method is called in the onCreate()
     */
    public void findViewByIDs() {
        itemName = findViewById(R.id.add_item);
        dueDate = findViewById(R.id.add_date_due);
        houseID = findViewById(R.id.add_item_house_id);
        borrowerID = findViewById(R.id.add_borrower_id);
        borrowedDate = findViewById(R.id.add_date_borrowed);
        itemImage = findViewById(R.id.add_image);
        cameraButton = findViewById(R.id.add_camera_button);
    }

    /**
     * Opens up the camera when a button is pressed
     * This method is called in the onCreate()
     */
    public void cameraButtonFunctionality() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * Sets the image of the item once the camera takes the picture
     * @param requestCode   The integer request code originally supplied to startActivityForResult(),
     *                      allowing you to identify who this result came from
     * @param resultCode    The integer result code returned by the child activity through its setResult()
     * @param data          An Intent, which can return result data to the caller
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        itemImage.setImageBitmap(bitmap);
    }

    /**
     * Uses DatePickerDialog to allow user to set a due date by using a calendar when adding an item
     * This method is called in the onCreate()
     */
    public void setDueDate() {

        mCurrentDate = Calendar.getInstance();

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        dueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        dueDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                        Log.d("DUE DATE", "DUE DATE: " + dueDate.toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    /**
     * Uses DatePickerDialog to allow user to set the borrowed date by using a calendar when adding an item
     * This method is called in the onCreate()
     */
    public void setBorrowedDate() {

        mCurrentDate = Calendar.getInstance();

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        borrowedDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemPage.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        borrowedDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    /**
     * Sets up all the variables and action bars necessary for the navigation bar
     * Called in the onCreate() method
     */
    public void drawerSetUp()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
     * Sets up the Navigation Bar and determines the "go to" location after an
     * item is clicked on the nav bar.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(AddItemPage.this, HomePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(AddItemPage.this, ProfilePage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(AddItemPage.this, MessagesPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_borrowed_items) {
            Intent intent = new Intent(AddItemPage.this, BorrowedItemsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (id == R.id.nav_dorms) {
            Intent intent = new Intent(AddItemPage.this, DormsPage.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;

    }

    /**
     *  Takes in the date in string format, and returns it in Epoch format
     * @param date              Date in String format
     * @return                  Return dates in Epoch form
     * @throws ParseException
     */
    private long getEpochFromDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime() / 1000L;
    }

    /**
     * Makes a GET request using the RESTAPI class for the current user's info
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
     * Parses the data retrieved from the database about the current user's information
     * Sets userIDFromRequest to the ID received from the database
     */
    public void parseUserInfo()
    {
        Log.d("DEBUG_PROFILE", "Made it to parseUserInfo");
        Log.d("DEBUG_PROFILE", userInfo.toString());
        try {

            userIDFromRequest = userInfo.getString("sub");
            Log.d("DEBUG_BORROWED_ITEMS", "UserID: " + userIDFromRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a POST request to add an item to the database
     * @param name              The name of the item being added
     * @param houseId           The house ID in which the item will be added to
     * @param borrowerId        The ID of the borrower
     * @param dateBorrowed      The date the item is borrowed
     * @param dueDate           The date the item is due
     * @param available         Determines whether or not an an item is available
     */
    public void postItem(String name, String houseId, String borrowerId, String dateBorrowed, String dueDate, boolean available)
    {
        RestAPI api = new RestAPI(rq);
        api.SetToken(token);

        // actual data
        JSONObject data = new JSONObject();
        try {
            data.put("ItemName",name);
            data.put("OwnerID", userIDFromRequest);
            data.put("ItemsHouseID", houseId);
            data.put("BorrowerID", borrowerId);
            data.put("DateBorrowed", getEpochFromDate(dateBorrowed));
            data.put("DateDue", getEpochFromDate(dueDate));
            data.put("Available",available);
            Log.d("POST ITEM JSON OBJECT", "Item POST request object: " + data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "date format is incorrect, needs to be yyyy-MM-dd", Toast.LENGTH_LONG);
            e.printStackTrace();
        }

        api.POST("/api/items", data, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject json) {
                // status, message

                try {
                    Integer status = (Integer) json.get("status");
                    String message = (String) json.get("message");

                    Log.d(TAG, "postItem success message: " + message);

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
                    Log.e(TAG, "postItem fail message: " + message);

                } catch (JSONException e) {
                    Log.e(TAG, "postItem fail data: " + data);
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


