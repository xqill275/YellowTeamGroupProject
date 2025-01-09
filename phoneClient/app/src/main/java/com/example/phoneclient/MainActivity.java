package com.example.phoneclient;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_NODES = 10; // Number of nodes to create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a root FrameLayout to position the NodeViews
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(rootLayout);

        Random random = new Random();

        for (int i = 0; i < NUM_NODES; i++) {
            // Create a new NodeView
            NodeView nodeView = new NodeView(this);

            // Generate random node number and colour
            int nodeNum = i + 1; // Unique node number
            int circleColor = Color.rgb(
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256)
            ); // Random RGB colour

            // Set the data for the NodeView
            nodeView.setNodeData(nodeNum, circleColor);

            // Generate random positions
            int size = 200; // Width and height of the circle (in pixels)
            int xPos = random.nextInt(getResources().getDisplayMetrics().widthPixels - size);
            int yPos = random.nextInt(getResources().getDisplayMetrics().heightPixels - size);

            // Set layout parameters for positioning
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = xPos;
            params.topMargin = yPos;
            nodeView.setLayoutParams(params);

            // Add the NodeView to the root layout
            rootLayout.addView(nodeView);
        }
    }
}