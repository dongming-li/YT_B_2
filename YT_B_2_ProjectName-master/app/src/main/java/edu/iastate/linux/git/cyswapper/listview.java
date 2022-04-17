package edu.iastate.linux.git.cyswapper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class listview extends AppCompatActivity {

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     * @param savedInstanceState    saves the previous instance of the activity to use when returning to it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
    }
}
