package com.example.phoneclient;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Random;

// The GameController manages the creation and display of nodes
public class GameController {
    private Context context;
    private FrameLayout rootLayout; // Layout to hold the nodes
    private static final int NODE_SIZE = 200; // Size of each node view in pixels

    // Constructor that accepts the context and root layout
    public GameController(Context context, FrameLayout rootLayout) {
        this.context = context;
        this.rootLayout = rootLayout;
    }

    // Method to create and display a specified number of nodes
    public void testNodes(int numNodes) {
        Random random = new Random();

        for (int i = 0; i < numNodes; i++) {
            // Create a new NodeView instance
            NodeView nodeView = new NodeView(context);

            // Generate random node data
            int nodeNum = i + 1; // Node number
            int circleColour = Color.rgb(
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256)
            ); // Random colour for the circle

            // Set the node's data
            nodeView.setNodeData(nodeNum, circleColour);

            // Calculate random positions
            int xPos = random.nextInt(rootLayout.getWidth() - NODE_SIZE);
            int yPos = random.nextInt(rootLayout.getHeight() - NODE_SIZE);

            // Set layout parameters for positioning
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(NODE_SIZE, NODE_SIZE);
            params.leftMargin = xPos;
            params.topMargin = yPos;
            nodeView.setLayoutParams(params);

            // Add the NodeView to the root layout
            rootLayout.addView(nodeView);
        }
    }
}
