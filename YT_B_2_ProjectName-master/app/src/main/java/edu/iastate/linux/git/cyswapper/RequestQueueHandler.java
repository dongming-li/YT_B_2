package edu.iastate.linux.git.cyswapper;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Created by Kristina on 10/7/2017.
 */

public class RequestQueueHandler extends Application
{
    /*TAGS for requests*/
    public static final String TAG = "request";

    /*RequestQueueThings*/
    private RequestQueue rq;
    public static RequestQueueHandler InstanceOfRequestQueue;

    /**
     * Initializes all the variables and methods needed for the Activity
     * Also, sets the layout of the Activity through the method setContentView()
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        InstanceOfRequestQueue = this;
    }

    /**
     * Returns instance of this class
     * @return
     */
    public static synchronized RequestQueueHandler getInstance()
    {
        return InstanceOfRequestQueue;
    }

    /**
     * Creates a request queue if one hasn't been created or uses the request that is already there
     * @return
     */
    public RequestQueue getRequestQueue()
    {
        if(rq == null)
        {
            rq = Volley.newRequestQueue(getApplicationContext());
        }
        return rq;
    }

    /**
     * allows a request to be added to the request queue
     * @param req JSON request
     * @param tag TAG
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req, String tag)
    {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    /**
     * allows a request to be added to the request queue
     * @param req JSON request
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests in request queue
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag)
    {
        if(rq != null)
        {
            rq.cancelAll(tag);
        }
    }
}