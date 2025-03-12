package com.example.phoneclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PickUsername extends AppCompatActivity {

    private EditText usernameInput;
    private Button joinButton;
    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_username);

        usernameInput = findViewById(R.id.editTextText2);
        joinButton = findViewById(R.id.button3);
        Button backButton2 = findViewById(R.id.backButton2);

        int gameId = getIntent().getIntExtra("gameId", -1);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = usernameInput.getText().toString();
                if (!playerName.isEmpty() && gameId != -1) {
                    joinGame(gameId, playerName);
                }
            }
        });

        backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will return to the previous activity
            }
        });
    }

    private void joinGame(int gameId, String playerName) {
        JSONObject json = new JSONObject();
        try {
            json.put("playerName", playerName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://trinity-developments.co.uk/games/" + gameId + "/players")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        int playerId = jsonResponse.getInt("playerId");
                        String message = jsonResponse.getString("message");
                        runOnUiThread(() -> {
                            System.out.println(message);
                            Intent intent = new Intent(PickUsername.this, LobbyScreen.class);
                            intent.putExtra("gameId", gameId);
                            intent.putExtra("playerId", playerId);
                            startActivity(intent);
                            finish();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}