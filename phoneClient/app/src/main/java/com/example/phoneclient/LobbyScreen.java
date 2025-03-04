package com.example.phoneclient;

import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    TextView playerNamesTextView;
    Button refreshButton;
    Handler handler = new Handler();
    Runnable refreshRunnable;
    int gameId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lobby_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        gameId = getIntent().getIntExtra("gameId", -1);
        int playerId = getIntent().getIntExtra("playerId", -1);
        playerNamesTextView = findViewById(R.id.playerNamesTextView);
        refreshButton = findViewById(R.id.refreshButton);

        Log.d(TAG, "Game ID: " + gameId);
        Log.d(TAG, "Host Player ID: " + playerId);

        fetchOpenGames(gameId);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchOpenGames(gameId);
            }
        });

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchOpenGames(gameId);
                handler.postDelayed(this, 10000);
            }
        };

        handler.post(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }

    public void fetchOpenGames(int targetGameId) {
        String url = BASE_URL + "games";
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch games: " + e.getMessage());
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
                            int gameId = game.getInt("gameId");

                            if (gameId == targetGameId) {
                                JSONArray players = game.getJSONArray("players");
                                StringBuilder playerNames = new StringBuilder();

                                for (int j = 0; j < players.length(); j++) {
                                    JSONObject player = players.getJSONObject(j);
                                    String playerName = player.getString("playerName");
                                    playerNames.append(playerName).append("\n");
                                }

                                String finalPlayerNames = playerNames.toString();
                                runOnUiThread(() -> playerNamesTextView.setText(finalPlayerNames));
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
}

