package com.example.phoneclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JoinGame extends AppCompatActivity {

    private Spinner gameSpinner;
    private Button joinButton;
    private ArrayList<String> gameNames = new ArrayList<>();
    private HashMap<String, Integer> gameIdMap = new HashMap<>();
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game2);

        gameSpinner = findViewById(R.id.spinner2);
        joinButton = findViewById(R.id.button2);
        Button backButton = findViewById(R.id.backButton); // 🔹 Add Back Button

        fetchGames();

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedGame = (String) gameSpinner.getSelectedItem();
                if (selectedGame != null && gameIdMap.containsKey(selectedGame)) {
                    int gameId = gameIdMap.get(selectedGame);
                    Intent intent = new Intent(JoinGame.this, PickUsername.class);
                    intent.putExtra("gameId", gameId);
                    startActivity(intent);
                }
            }
        });

        // 🔹 Back Button Logic
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes this activity and goes back to the previous screen
            }
        });
    }

    private void fetchGames() {
        Request request = new Request.Builder()
                .url("http://trinity-developments.co.uk/games")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        JSONObject jsonResponse = new JSONObject(result);
                        JSONArray gamesArray = jsonResponse.getJSONArray("games");

                        for (int i = 0; i < gamesArray.length(); i++) {
                            JSONObject game = gamesArray.getJSONObject(i);
                            String gameName = game.getString("gameName");
                            int gameId = game.getInt("gameId");
                            gameNames.add(gameName);
                            gameIdMap.put(gameName, gameId);
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(JoinGame.this, android.R.layout.simple_spinner_item, gameNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            gameSpinner.setAdapter(adapter);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
