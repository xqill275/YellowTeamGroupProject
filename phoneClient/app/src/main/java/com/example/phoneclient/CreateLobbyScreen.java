package com.example.phoneclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateLobbyScreen extends AppCompatActivity {
    Button createGameButton;
    EditText gameNameInput;
    Spinner mapSpinner;
    Switch gameLengthSwitch;
    ArrayAdapter<String> mapAdapter;
    OkHttpClient client = new OkHttpClient();
    private static final String TAG = "CreateLobbyScreen";
    String BASE_URL = "http://trinity-developments.co.uk/";
    ArrayList<Integer> mapIds; // Store Map IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_lobby_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameNameInput = findViewById(R.id.editTextText);
        createGameButton = findViewById(R.id.button);
        mapSpinner = findViewById(R.id.spinner);
        gameLengthSwitch = findViewById(R.id.switch1);

        fetchMaps();

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameName = gameNameInput.getText().toString();
                boolean shortGame = gameLengthSwitch.isChecked();

                // Ensure valid selection
                int selectedIndex = mapSpinner.getSelectedItemPosition();
                if (selectedIndex < 0 || selectedIndex >= mapIds.size()) {
                    Toast.makeText(CreateLobbyScreen.this, "Invalid map selection", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedMapId = mapIds.get(selectedIndex); // Get the correct Map ID

                if (gameName.isEmpty()) {
                    Toast.makeText(CreateLobbyScreen.this, "Please enter a game name", Toast.LENGTH_SHORT).show();
                    return;
                }

                createGame(gameName, selectedMapId, shortGame);
            }
        });
    }

    public void fetchMaps() {
        String url = BASE_URL + "maps";
        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch maps: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(CreateLobbyScreen.this, "Failed to load maps", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        Log.d(TAG, "Maps Response: " + responseData);

                        JSONArray maps = new JSONArray(responseData);
                        ArrayList<String> mapNames = new ArrayList<>();
                        mapIds = new ArrayList<>();

                        for (int i = 0; i < maps.length(); i++) {
                            JSONObject map = maps.getJSONObject(i);

                            if (map.has("mapName") && map.has("mapId")) {
                                mapNames.add(map.getString("mapName"));
                                mapIds.add(map.getInt("mapId")); // Store correct map ID
                            } else {
                                Log.w(TAG, "Map JSON Missing 'mapName' or 'mapId' at index " + i);
                            }
                        }

                        runOnUiThread(() -> {
                            mapAdapter = new ArrayAdapter<>(CreateLobbyScreen.this, android.R.layout.simple_spinner_item, mapNames);
                            mapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mapSpinner.setAdapter(mapAdapter);
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(CreateLobbyScreen.this, "JSON Parsing Error", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch maps: " + response.code());
                }
            }
        });
    }

    public void createGame(String gameName, int mapID, boolean shortGame) {
        String url = BASE_URL + "games";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", gameName);
            requestBody.put("mapId", mapID);
            requestBody.put("gameLength", shortGame ? "short" : "long");

            RequestBody body = RequestBody.create(requestBody.toString(), JSON);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network Error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(CreateLobbyScreen.this, "Failed to create game", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response: " + responseData);

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            int gameId = jsonResponse.getInt("gameId");

                            joinHostPlayer(gameId);

                        } catch (Exception e) {
                            Log.e(TAG, "JSON Error: " + e.getMessage());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(CreateLobbyScreen.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Request Error: " + e.getMessage());
        }
    }

    public void joinHostPlayer(int gameId) {
        Log.e(TAG, "Trying to create host player");
        String url = BASE_URL + "games/" + gameId + "/players";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("playerName", "Host");

            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to join Host: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            int playerId = jsonResponse.getInt("playerId");
                            Log.e(TAG, "playerID: "+playerId);

                            runOnUiThread(() -> {
                                Intent intent = new Intent(CreateLobbyScreen.this, LobbyScreen.class);
                                intent.putExtra("gameId", gameId);
                                intent.putExtra("playerId", playerId);
                                startActivity(intent);
                                finish();
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Request Error: " + e.getMessage());
        }
    }
}
