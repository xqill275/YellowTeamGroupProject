package com.example.phoneclient;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    int gameId, mapId, playerId; // Store intents here
    GameController gc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read Intents 🔥
        gameId = getIntent().getIntExtra("gameId", -1);
        mapId = getIntent().getIntExtra("mapId", -1);
        playerId = getIntent().getIntExtra("playerId", -1);

        Log.d(TAG, "Game ID: " + gameId);
        Log.d(TAG, "Map ID: " + mapId);
        Log.d(TAG, "Player ID: " + playerId);

        // Set Background Image
        Drawable BGimage = ResourcesCompat.getDrawable(getResources(), R.drawable.game_map1, null);
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        rootLayout.setBackground(BGimage);
        setContentView(rootLayout);

        // Create GameController
        gc = new GameController(this, rootLayout);

        // Start the game with the correct mapId
        rootLayout.post(() -> {
            Log.d(TAG, "Starting Game with Map ID: " + mapId);
            gc.startGame(1); // Pass the mapId to startGame
        });
    }
}
