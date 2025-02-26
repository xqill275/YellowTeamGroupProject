package com.example.phoneclient;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.io.IOException;




public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set background image
        Drawable BGimage = ResourcesCompat.getDrawable(getResources(), R.drawable.game_map1, null);

        // Create a root FrameLayout to hold the nodes
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        rootLayout.setBackground(BGimage);
        setContentView(rootLayout);
        GameController gc = new GameController(this, rootLayout);
        // Fetch map data from server

        rootLayout.post(() -> {
            gc.startGame(901);
        });
    }

}
