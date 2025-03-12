package com.example.phoneclient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    int hostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get mapId, playerId, and gameId from Intent
        mapId = getIntent().getIntExtra("mapId", -1);
        playerId = getIntent().getIntExtra("playerId", -1);
        gameId = getIntent().getIntExtra("gameId", -1);
        hostStartLocation = getIntent().getIntExtra("HostStartLocation", -1);
        hostID = getIntent().getIntExtra("HostID", -1);

        Log.d(TAG, "host Start Location: " + hostStartLocation);
        Log.d(TAG, "Received mapId: " + mapId + ", playerId: " + playerId + ", gameId: " + gameId + " host id: "+hostID);

        // Create root FrameLayout for game content
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT // Allows content to grow
        ));

        // UI Frame (Overlay) that should follow scrolling
        FrameLayout uiFrame = new FrameLayout(this);
        uiFrame.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // HorizontalScrollView for horizontal scrolling
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        horizontalScrollView.setLayoutParams(new HorizontalScrollView.LayoutParams(
                HorizontalScrollView.LayoutParams.MATCH_PARENT,
                HorizontalScrollView.LayoutParams.MATCH_PARENT
        ));
        horizontalScrollView.addView(rootLayout);

        // ScrollView for vertical scrolling
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.MATCH_PARENT
        ));
        scrollView.addView(horizontalScrollView);

        // Set the main content view
        setContentView(scrollView);

        // Initialize GameController and pass required parameters
        GameController gc = new GameController(this, rootLayout, uiFrame, gameId, hostStartLocation, playerId, hostID);

        // Make `uiFrame` follow the scrolling position
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                uiFrame.setTranslationY(scrollY);
            }
        });

        horizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                uiFrame.setTranslationX(scrollX);
            }
        });

        // Start the game after a delay to allow UI to initialize
        rootLayout.postDelayed(() -> gc.startGame(mapId), 1000);

    }
}
