package com.example.phoneclient;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// The GameController manages the creation and display of nodes
public class GameController {
    private Context context;
    private FrameLayout rootLayout; // Layout to hold the nodes
    private static final int NODE_SIZE = 200; // Size of each node view in pixels
    private List<Node> nodes; // List to hold Node objects

    // Constructor that accepts the context and root layout
    public GameController(Context context, FrameLayout rootLayout) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.nodes = new ArrayList<>();
    }

    // Method to create and display a specified number of nodes
    public void testNodes(int numNodes) {
        for (int i = 0; i < numNodes; i++) {
            Node node = createNode(i);
            nodes.add(node);

            NodeView nodeView = createNodeView(node);
            positionNodeView(nodeView);

            rootLayout.addView(nodeView);
        }
    }

    // Creates a new Node instance
    private Node createNode(int index) {
        int nodeNum = index + 1; // Node number
        String[] acceptedTravelMethods = {"Walk", "Bus", "Train"}; // Example travel methods
        int[] connectedNodes = {}; // Initially no connected nodes
        String[] stationColours = {"Red", "Blue"}; // Example colours

        return new Node(nodeNum, acceptedTravelMethods, connectedNodes, stationColours);
    }

    // Creates and configures a NodeView instance
    private NodeView createNodeView(Node node) {
        NodeView nodeView = new NodeView(context);
        int circleColour = generateRandomColor();
        nodeView.setNodeData(node.getNodeNum(), circleColour);
        return nodeView;
    }

    // Positions the NodeView randomly within the root layout
    private void positionNodeView(NodeView nodeView) {
        Random random = new Random();
        int xPos = Math.max(0, random.nextInt(rootLayout.getWidth() - NODE_SIZE));
        int yPos = Math.max(0, random.nextInt(rootLayout.getHeight() - NODE_SIZE));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(NODE_SIZE, NODE_SIZE);
        params.leftMargin = xPos;
        params.topMargin = yPos;
        nodeView.setLayoutParams(params);
    }

    // Generates a random RGB color
    private int generateRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    // Getter for the list of nodes
    public List<Node> getNodes() {
        return nodes;
    }
}
