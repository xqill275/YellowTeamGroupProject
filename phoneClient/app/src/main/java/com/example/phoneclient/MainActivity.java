package com.example.phoneclient;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get mapId from Intent
        int mapId = getIntent().getIntExtra("mapId", -1);
        Log.d(TAG, "Received mapId: " + mapId);

        // Create root layout
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(rootLayout);

        // Initialize GameController
        GameController gc = new GameController(this, rootLayout);

        // Start the game with the received mapId
        rootLayout.post(() -> gc.startGame(mapId));
    }
}
