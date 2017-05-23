package it.parello.magnetstatereceiver.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Parello on 20/04/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
//    Context applicationContext = MyAppActivity.getContextOfApplication();

    //Set with your local ip address and the port you selected in the node server.js
    private static final String URL = "http://192.168.1.116:8888/token";


    public static String refreshedToken;
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }


    public String sendRegistrationToServer(String token){
//        saveTokenToPreferences(token);
        HttpURLConnection connection = null;
        URL url;
        InputStream stream = null;
        try {
            url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            Log.d(TAG,"Connected to " + URL);
            Log.e("TOKEN SENT TO SERVER", token);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(token);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
            String result = reader.readLine();
            Log.e(TAG,"RESULT " +result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

//    public void saveTokenToPreferences(String token){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("firebase-token", token);
//        editor.commit();
//    }
}
