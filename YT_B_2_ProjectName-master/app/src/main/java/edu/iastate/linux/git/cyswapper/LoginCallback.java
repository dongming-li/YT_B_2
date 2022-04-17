package edu.iastate.linux.git.cyswapper;

/**
 * Created by laytonnelson on 11/6/17.
 */

public interface LoginCallback {
    /**
     * onSuccess happens when the login succeeds
     */
    void onSuccess();

    /**
     * onFail happens when the login fails
     */
    void onFail();

    /**
     * When a toast needs to be created
     * @param message
     */
    void onToast(String message);
}
