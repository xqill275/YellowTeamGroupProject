package com.example.phoneclient;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL = "http://trinity-developments.co.uk"; // Replace with actual URL

    private FrameLayout rootLayout;
    int mapId, playerId, gameId, hostStartLocation, hostID;

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
        Log.d(TAG, "Received mapId: " + mapId + ", playerId: " + playerId + ", gameId: " + gameId + " host id: " + hostID);

        // Create root FrameLayout for game content
        rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Fetch and set map background
        fetchMapDetails();

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

        setContentView(scrollView);

        // Add `uiFrame` on top of everything
        addContentView(uiFrame, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Ensure `uiFrame` stays on top
        uiFrame.bringToFront();

        // Initialize GameController and pass required parameters
        GameController gc = new GameController(this, rootLayout, uiFrame, gameId, hostStartLocation, playerId, hostID);


        // Start the game after a delay to allow UI to initialize
        rootLayout.postDelayed(() -> gc.startGame(mapId), 1000);
    }

    private void fetchMapDetails() {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(BASE_URL + "/maps/" + mapId)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();

                    // Parse JSON response using JSONObject
                    JSONObject jsonResponse = new JSONObject(responseData);

                    // Extract the map image URL
                    if (jsonResponse.has("mapImage")) {
                        String mapImagePath = jsonResponse.getString("mapImage");
                        String fullMapUrl = mapImagePath;

                        // Set background image on UI thread
                        runOnUiThread(() -> setBackgroundImage(fullMapUrl));
                    }
                } else {
                    Log.e(TAG, "Failed to fetch map details: " + response.message());
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching map details", e);
            }
        }).start();
    }

    private void setBackgroundImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        rootLayout.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) { }
                });
    }
}
