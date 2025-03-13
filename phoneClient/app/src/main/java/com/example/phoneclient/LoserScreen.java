package com.example.phoneclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class LoserScreen extends AppCompatActivity {

    private static final String TAG = "LoserScreen";
    private static final String BASE_GAME_URL = "http://http://trinity-developments.co.uk/game/"; // Replace with actual API URL
    private int gameID; // Ensure you pass this from the previous activity
    private TextView lossText;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loser_screen);

        lossText = findViewById(R.id.LossText);
        Button replayButton = findViewById(R.id.replayButton2);

        // Get game ID from intent
        gameID = getIntent().getIntExtra("GAME_ID", -1);
        if (gameID != -1) {
            fetchWinner(gameID);
        } else {
            Log.e(TAG, "Invalid Game ID received");
        }

        // Replay button logic (Modify as needed)
        replayButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoserScreen.this, MainMenu.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchWinner(int gameId) {
        String url = BASE_GAME_URL + gameId;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch game state", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected response: " + response);
                    return;
                }

                String responseData = response.body().string();
                Log.d(TAG, "Game State Response: " + responseData);

                try {
                    JSONObject gameObject = new JSONObject(responseData);
                    String winner = gameObject.getString("winner");

                    // Update UI on main thread
                    runOnUiThread(() -> lossText.setText("GAME OVER! \n\n The winner is: " + winner));

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing game state", e);
                }
            }
        });
    }
}
