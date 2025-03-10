package com.example.phoneclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LobbyScreen extends AppCompatActivity {

    private static final String TAG = "LobbyScreen";
    String BASE_URL = "http://trinity-developments.co.uk/";
    OkHttpClient client = new OkHttpClient();

    TextView playerNamesTextView, waitingTextView;
    Button startGameButton;
    ProgressBar loadingSpinner;
    Handler handler = new Handler();
    Runnable refreshRunnable;

    int gameId, playerId, hostPlayerId = -1, mapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_screen);

        gameId = getIntent().getIntExtra("gameId", -1);
        playerId = getIntent().getIntExtra("playerId", -1);

        playerNamesTextView = findViewById(R.id.playerNamesTextView);
        waitingTextView = findViewById(R.id.waitingTextView);
        startGameButton = findViewById(R.id.startGameButton);
        loadingSpinner = findViewById(R.id.loadingSpinner);

        startGameButton.setVisibility(View.GONE);

        fetchOpenGames(gameId);

        startGameButton.setOnClickListener(v -> startGame(gameId, playerId));

        refreshRunnable = () -> {
            fetchOpenGames(gameId);
            handler.postDelayed(refreshRunnable, 10000); // Refresh every 10 seconds
        };
        handler.post(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }

    public void fetchOpenGames(int targetGameId) {
        Request request = new Request.Builder()
                .url(BASE_URL + "games")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching games: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray games = jsonResponse.getJSONArray("games");

                        for (int i = 0; i < games.length(); i++) {
                            JSONObject game = games.getJSONObject(i);
                            int currentGameId = game.getInt("gameId");

                            if (currentGameId == targetGameId) {
                                mapId = game.getInt("mapId");
                                Log.d(TAG, "Fetched Map ID: " + mapId); // ✅ Log mapId

                                JSONArray players = game.getJSONArray("players");
                                StringBuilder playerNames = new StringBuilder();

                                Log.d(TAG, "Fetched " + players.length() + " players");

                                for (int j = 0; j < players.length(); j++) {
                                    JSONObject player = players.getJSONObject(j);
                                    String playerName = player.getString("playerName");
                                    int playerID = player.getInt("playerId");

                                    Log.d(TAG, "Player " + j + ": " + playerName + " (playerId: " + playerID + ")");

                                    if (playerName.equals("Host")) {
                                        hostPlayerId = playerID;
                                    }

                                    playerNames.append(playerName).append("\n");
                                }

                                Log.d(TAG, "Updated player names:\n" + playerNames);

                                runOnUiThread(() -> {
                                    playerNamesTextView.setText(playerNames.toString());
                                    playerNamesTextView.setAlpha(0f);
                                    playerNamesTextView.animate().alpha(1f).setDuration(300);
                                });

                                if (playerId == hostPlayerId) {
                                    Log.d(TAG, "Current player is the Host with playerId: " + playerId);
                                    runOnUiThread(() -> {
                                        startGameButton.setVisibility(View.VISIBLE);
                                        waitingTextView.setVisibility(View.GONE);
                                    });
                                } else {
                                    Log.d(TAG, "Current player is NOT the Host");
                                    runOnUiThread(() -> {
                                        waitingTextView.setVisibility(View.VISIBLE);
                                        waitingTextView.setText("Waiting for Host to Start the Game...");
                                        startGameButton.setVisibility(View.GONE);
                                    });
                                }
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch games: " + response.code());
                }
            }
        });
    }

    public void startGame(int gameId, int playerId) {
        Request request = new Request.Builder()
                .url(BASE_URL + "games/" + gameId + "/start/" + playerId)
                .patch(okhttp3.RequestBody.create("", okhttp3.MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to start game: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Game started: " + responseData);

                    runOnUiThread(() -> {
                        Intent intent = new Intent(LobbyScreen.this, MainActivity.class);
                        intent.putExtra("gameId", gameId);
                        intent.putExtra("mapId", mapId);
                        intent.putExtra("playerId", playerId);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Log.e(TAG, "Failed to start game: " + response.code());
                }
            }
        });
    }
}
