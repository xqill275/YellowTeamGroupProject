package com.example.phoneclient;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        int image = R.drawable.game_map1; // change this to chnage background image of the app
        Drawable BGimage = ResourcesCompat.getDrawable(res, image, null);
        super.onCreate(savedInstanceState);

        // Create a root FrameLayout to hold the nodes
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(rootLayout);


        //rootLayout.setBackground(getResources().getColor(android.R.color.holo_red_dark));
        rootLayout.setBackground(BGimage);

        // Create an instance of GameController
        GameController gameController = new GameController(this, rootLayout);

        // Ensure testNodes is called after the layout is measured
        rootLayout.post(() -> {
            // Use GameController to create and display 10 nodes
            gameController.testNodes(7);

            // Example: Access the list of Node objects
            for (Node node : gameController.getNodes()) {
                // You can log or manipulate node data here
                System.out.println("Node number: " + node.getNodeNum());
            }
        });
    }
}
