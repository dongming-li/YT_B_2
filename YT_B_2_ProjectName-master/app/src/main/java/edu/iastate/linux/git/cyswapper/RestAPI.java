package edu.iastate.linux.git.cyswapper;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by laytonnelson on 11/5/17.
 * This class should help us do requests to the REST API
 * <p>
 * Please see the following page to refresh on what a URL is made of:
 * https://www.ibm.com/support/knowledgecenter/en/SSGMCP_5.1.0/com.ibm.cics.ts.internet.doc/topics/dfhtl_uricomp.html
 */

public class RestAPI {
    private String token;
    private String url;
    private String authpath;

    // Tag for log statements
    private final String TAG = RestAPI.class.getSimpleName();

    /**
     * rq will hold the Volley RequestQueue for debug purposes
     */
    public RequestQueue rq;


    /**
     * RestAPI will setup a new class to make requests to a given API URL
     *
     * @param rq       Volley RequestQueue
     * @param url      url and port of domain to use
     * @param authpath path for POSTing OAuth code to receive JWT token
     * @param token    preset the api token
     */
    public RestAPI(RequestQueue rq, String url, String authpath, String token) {
        this.url = url;
        this.authpath = authpath;
        this.rq = rq;
        this.token = token;
        //Log.d("IS THE TOKEN SET?", "RestAPI() token = " + token.toString());
    }

    /**
     * RestAPI will setup a new class to make requests to a given API URL
     * <p>
     * Example:
     * RestAPI api = new RestAPI("http://proj-309-yt-b-2.cs.iastate.edu:8080/api", "/authcode");
     *
     * @param rq       Volley RequestQueue
     * @param url      url and port of domain to use
     * @param authpath path for POSTing OAuth code to receive JWT token
     */
    public RestAPI(RequestQueue rq, String url, String authpath) {
        this(rq, url, authpath, "");
    }

    /**
     * RestAPI lazy constructor for the lazy people, with token param
     * It will set all of the required things for us, since laziness is key
     *
     * @param rq    Volley RequestQueue
     * @param token preset the api token
     */
    public RestAPI(RequestQueue rq, String token) {
        this(rq, "http://proj-309-yt-b-2.cs.iastate.edu:8080", "/authcode", token);
    }

    /**
     * RestAPI lazy constructor for the lazy people, without token param
     * It will set all of the required things for us, since laziness is key
     *
     * @param rq Volley RequestQueue
     */
    public RestAPI(RequestQueue rq) {
        this(rq, "");
    }


    /**
     * DecodeJSONError will decode the message of the json error
     *
     * @param json JSON object of error
     */
    public String DecodeJSONError(JSONObject json) {
        try {
            Integer jstatus = (Integer) json.get("status");
            String jmessage = (String) json.get("message");

            Log.d(TAG, String.format("[JSON ERROR]: Status: %s", jstatus));
            Log.d(TAG, String.format("[JSON ERROR]: Status: %s", jmessage));
            return jmessage;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SetToken setter will manually set a new token
     *
     * @param token JWT token
     */
    public void SetToken(String token) {
        this.token = token;
    }

    public String GetToken() {
        return token;
    }

    /**
     * Login will login to the api and get a token
     *
     * @param code     will come from GoogleSignInAccount.getServerAuthCode()
     * @param callback LoginCallback
     */
    public void Login(String code, final LoginCallback callback) {
        // clear old token, so the headers wont be used
        String oldToken = this.token; // unused i guess
        this.token = "";

        // setup params for POST
        Map<String, String> params = new HashMap<>();
        params.put("authCode", code);

        // get json from POST
        POST(authpath, params, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject json) {
                if (json == null) {
                    Log.e(TAG, "Login failed, json is null.");
                    callback.onFail();
                    return;
                }

                String jtoken;

                try {
                    jtoken = (String) json.get("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Login failed, could not parse token from json.");
                    callback.onFail();
                    return;
                }

                // set the token
                SetToken(jtoken);
                Log.e(TAG, "Login success.");
                callback.onSuccess();
                return;
            }

            @Override
            public void onFailResponse(NetworkResponse response) {
                callback.onFail();
            }

            @Override
            public void onError() {
                callback.onFail();
            }

            @Override
            public void onToast(String message) {
                callback.onToast(message);
            }
        });

    }

    /**
     * GET will perform a GET request for a path with params
     *
     * @param path     from api url, example: /api/items
     * @param params   params to use
     * @param callback VolleyCallback to return data
     */
    public void GET(String path, final Map<String, String> params, final VolleyCallback callback) {
        StringRequest r = new StringRequest(Request.Method.GET, this.url + path, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                Log.d(TAG, "GET Response: " + resp);
                try {
                    callback.onSuccessResponse(new JSONObject(resp));
                } catch (JSONException e) {
                    callback.onError();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        theErrorResponse("GET", error, callback);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return theHeaders();
            }
        };

        rq.add(r);
    }

    /**
     * GET will perform a GET request for a path
     *
     * @param path from api url, example: /api/items
     */
    public void GET(String path, final VolleyCallback callback) {
        GET(path, null, callback);
    }

    /**
     * POST will perform a POST request for a path with json data
     *
     * @param path     from api url, example: /api/items
     * @param data     JSON data to send
     * @param callback VolleyCallback to return data
     */
    public void POST(String path, final JSONObject data, final VolleyCallback callback) {
        String theUrl = this.url + path;
        Log.d(TAG, "POST going to url: " + theUrl);

        StringRequest r = new StringRequest(Request.Method.POST, theUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                Log.d(TAG, "POST Response: " + resp);
                try {
                    callback.onSuccessResponse(new JSONObject(resp));
                } catch (JSONException e) {
                    callback.onError();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        theErrorResponse("POST", error, callback);
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                if (theBody(data) == null) Log.d(TAG, "Data is null!");
                else Log.d(TAG, "Data is not null!");
                return theBody(data);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "Headers are: " + theHeaders());
                return theHeaders();
            }
        };

        rq.add(r);
    }

    /**
     * POST will perform a POST request for a path with params
     *
     * @param path     from api url, example: /api/items
     * @param params   params for POST request
     * @param callback VolleyCallback to return data
     */
    public void POST(String path, final Map<String, String> params, final VolleyCallback callback) {
        String theUrl = this.url + path;
        Log.d(TAG, "POST going to url: " + theUrl);

        StringRequest r = new StringRequest(Request.Method.POST, theUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                Log.d(TAG, "POST Response: " + resp);
                try {
                    callback.onSuccessResponse(new JSONObject(resp));
                } catch (JSONException e) {
                    callback.onError();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        theErrorResponse("POST", error, callback);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Log.d(TAG, "Params are: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "Headers are: " + theHeaders());
                return theHeaders();
            }
        };

        rq.add(r);
    }

    /**
     * DELETE will perform a DELETE request for a path
     *
     * @param path     from api url, example: /api/items/11
     * @param callback VolleyCallback to return data
     */
    public void DELETE(String path, final VolleyCallback callback) {
        StringRequest r = new StringRequest(Request.Method.DELETE, this.url + path, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                Log.d(TAG, "DELETE Response: " + resp);
                try {
                    callback.onSuccessResponse(new JSONObject(resp));
                } catch (JSONException e) {
                    callback.onError();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        theErrorResponse("DELETE", error, callback);
                    }
                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                return params;
//            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return theHeaders();
            }
        };

        rq.add(r);
    }

    /**
     * PATCH will perform a PATCH request for a path with JSON data
     *
     * @param path from api url, example: /api/items/11
     * @param data JSON data to send
     */
    public void PATCH(String path, final JSONObject data, final VolleyCallback callback) {
        StringRequest r = new StringRequest(Request.Method.PATCH, this.url + path, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                Log.d(TAG, "PATCH Response: " + resp);
                try {
                    callback.onSuccessResponse(new JSONObject(resp));
                } catch (JSONException e) {
                    callback.onError();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        theErrorResponse("PATCH", error, callback);
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                return theBody(data);
            }

//            @Override
//            protected Map<String, String> getParams() {
//                return params;
//            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return theHeaders();
            }
        };

        rq.add(r);
    }

    /**
     * theHeaders is a helper function to return auth headers if applicable
     *
     * @return headers for getHeaders()
     */
    private Map<String, String> theHeaders() {
        Map<String, String> h = new HashMap<>();

        Log.d(TAG, "POST Made it into getHeaders()");

        // set auth header if token exists
        if (token != null && !Objects.equals(token, "")) {
            Log.d(TAG, "POST set auth header token");
            h.put("Authorization", "Bearer " + token);
            Log.d("TOKEN HEADER_HUH??? YES NOO?!?", "token (h): " + h);
        }
        Log.d("TOKEN HEADER???", "token (h): " + h);
        return h;
    }

    /**
     * theErrorResponse is a helper function to be used in onErrorResponse()
     *
     * @param reqType  type of request that is being made, used for logs
     * @param error    VolleyError
     * @param callback VolleyCallback
     */
    private void theErrorResponse(String reqType, VolleyError error, final VolleyCallback callback) {
        Log.e(TAG, String.format("%s Error: %s", reqType, error.toString()));
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            Log.e(TAG, String.format("%s Status: %s", reqType, response.statusCode));
            Log.e(TAG, String.format("%s Headers: %s", reqType, response.headers.toString()));
            Log.e(TAG, String.format("%s Data: %s", reqType, new String(response.data)));

            callback.onFailResponse(response);

            try {
                String respErr = new String(response.data);
                String message;
                if (!respErr.contains("404 page not found")) {
                    message = DecodeJSONError(new JSONObject(respErr));
                } else {
                    message = respErr;
                }

                // print toast
                callback.onToast(message);
            } catch (JSONException e) {
                callback.onError();
                e.printStackTrace();
            }

        } else {
            callback.onError();
        }
    }

    /**
     * theBody is a helper function to be used in getBody()
     *
     * @param data JSONObject
     * @return bytes
     */
    private byte[] theBody(final JSONObject data) throws AuthFailureError {
        if (data == null) return null;

        String requestBody = data.toString();
        try {
            return requestBody == null ? null : requestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            Log.d(TAG, String.format("Unsupported Encoding while trying to get the bytes of %s using %s",
                    requestBody, "utf-8"));
            return null;
        }
    }
}