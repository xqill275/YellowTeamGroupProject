package com.example.phoneclient;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    int mapId;
    int playerId;
    int gameId;
    int hostStartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get mapId, playerId, and gameId from Intent
        mapId = getIntent().getIntExtra("mapId", -1);
        playerId = getIntent().getIntExtra("playerId", -1);
        gameId = getIntent().getIntExtra("gameId", -1);
        hostStartLocation = getIntent().getIntExtra("HostStartLocation", -1);

        Log.d(TAG, "host Start Location: " + hostStartLocation);
        Log.d(TAG, "Received mapId: " + mapId + ", playerId: " + playerId + ", gameId: " + gameId);

        // Create root FrameLayout
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT // Allows content to grow
        ));

        FrameLayout uiFrame = new FrameLayout(this);
        uiFrame.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Wrap rootLayout inside a HorizontalScrollView to allow horizontal scrolling
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        horizontalScrollView.setLayoutParams(new HorizontalScrollView.LayoutParams(
                HorizontalScrollView.LayoutParams.MATCH_PARENT,
                HorizontalScrollView.LayoutParams.MATCH_PARENT
        ));
        horizontalScrollView.addView(rootLayout);

        // Wrap the HorizontalScrollView inside a ScrollView to allow vertical scrolling
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.MATCH_PARENT
        ));
        scrollView.addView(horizontalScrollView);

        setContentView(scrollView);

        // Initialize GameController and pass gameId
        GameController gc = new GameController(this, rootLayout, uiFrame, gameId, hostStartLocation, playerId);

        // Start the game with the received mapId
        rootLayout.post(() -> gc.startGame(mapId));  // Start game with mapId
    }
}
