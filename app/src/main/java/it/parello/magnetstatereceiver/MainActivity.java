package it.parello.magnetstatereceiver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import it.parello.magnetstatereceiver.firebase.MyFirebaseInstanceIdService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(MyFirebaseInstanceIdService.refreshedToken != null)
        Log.e(TAG, MyFirebaseInstanceIdService.refreshedToken);
    }
}
