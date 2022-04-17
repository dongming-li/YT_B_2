package edu.iastate.linux.git.cyswapper;

import com.android.volley.NetworkResponse;

import org.json.JSONObject;

/**
 * Created by laytonnelson on 11/6/17.
 */

public interface VolleyCallback {
    /**
     * onSuccessResponse happens when Volley has a successful response
     * @param json data from response
     */
    void onSuccessResponse(JSONObject json);

    /**
     * onFailResponse happens when Volley encounters an error
     * @param response has the network data that gave the error
     */
    void onFailResponse(NetworkResponse response);

    /**
     * onError happens when an unknown error occurs in Volley
     */
    void onError();

    /**
     * When a toast needs to be created
     * @param message
     */
    void onToast(String message);
}
