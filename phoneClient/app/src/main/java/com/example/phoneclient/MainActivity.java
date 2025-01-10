package com.example.phoneclient;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a root FrameLayout to hold the nodes
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(rootLayout);

        // Create an instance of GameController
        GameController gameController = new GameController(this, rootLayout);

        // Ensure testNodes is called after the layout is measured
        rootLayout.post(() -> {
            // Use GameController to create and display 10 nodes
            gameController.testNodes(10);

            // Example: Access the list of Node objects
            for (Node node : gameController.getNodes()) {
                // You can log or manipulate node data here
                System.out.println("Node number: " + node.getNodeNum());
            }
        });
    }
}
