package com.example.phoneclient;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// The GameController manages the creation and display of nodes
public class GameController {
    private Context context;
    private FrameLayout rootLayout; // Layout to hold the nodes
    private static final int NODE_SIZE = 200; // Size of each node view in pixels
    private List<Node> nodes; // List to hold NODE objects
    private static final String BASE_MAP_URL = "http://trinity-developments.co.uk/maps/";
    private static final String TAG = "GameController";
    private OkHttpClient client = new OkHttpClient();

    private int screenWidth;
    private int screenHeight;

    private int mapWidth;
    private int mapHeight;

    // Constructor that accepts the context and root layout
    public GameController(Context context, FrameLayout rootLayout) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.nodes = new ArrayList<>();
        getScreenDimensions();
    }

    private void getScreenDimensions() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    public void startGame(int mapId) {
        getMapData(mapId);
    }

    private void getMapData(int mapId) {
        String url = BASE_MAP_URL + mapId;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch data", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected code " + response);
                    return;
                }

                String responseData = response.body().string();
                Log.d(TAG, "Response: " + responseData);
                parseMapData(responseData);
            }
        });
    }

    private void parseMapData(String jsonData) {
        try {
            JSONObject mapObject = new JSONObject(jsonData);

            // Extract map dimensions
            mapWidth = mapObject.getInt("mapWidth");
            mapHeight = mapObject.getInt("mapHeight");

            JSONArray locationsArray = mapObject.getJSONArray("locations");
            JSONArray connectionsArray = mapObject.getJSONArray("connections");

            HashMap<Integer, String> nodeColours = new HashMap<>();
            for (int i = 0; i < connectionsArray.length(); i++) {
                JSONObject connectionObject = connectionsArray.getJSONObject(i);
                int locationA = connectionObject.getInt("locationA");
                int locationB = connectionObject.getInt("locationB");
                String ticketColour = connectionObject.getString("ticket");

                nodeColours.put(locationA, ticketColour);
                nodeColours.put(locationB, ticketColour);
            }
            // Parse locations (nodes)
            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject nodeObject = locationsArray.getJSONObject(i);
                int nodeNum = nodeObject.getInt("location");
                int xPos = nodeObject.getInt("xPos");
                int yPos = nodeObject.getInt("yPos");


                // Scale the positions
                int scaledX = (int) ((xPos / (float) mapWidth) * screenWidth);
                int scaledY = (int) ((yPos / (float) mapHeight) * screenHeight);

                String colourName = nodeColours.getOrDefault(nodeNum, "Grey");
                int colour = getColourFromName(colourName);

                Node node = createNode(nodeNum, scaledX, scaledY);
                nodes.add(node);

                NodeView nodeView = createNodeView(node, colour);
                positionNodeView(nodeView, scaledX, scaledY);

                rootLayout.post(() -> rootLayout.addView(nodeView));
            }

            // Parse connections and draw them
            drawConnections(connectionsArray);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
    }

    private void drawConnections(JSONArray connectionsArray) throws JSONException {
        for (int i = 0; i < connectionsArray.length(); i++) {
            JSONObject connectionObject = connectionsArray.getJSONObject(i);
            int locationA = connectionObject.getInt("locationA");
            int locationB = connectionObject.getInt("locationB");
            String ticketColour = connectionObject.getString("ticket");

            // Get the corresponding nodes
            Node nodeA = getNodeById(locationA);
            Node nodeB = getNodeById(locationB);

            // If both nodes exist, draw a connection (a line)
            if (nodeA != null && nodeB != null) {
                int colour = getColourFromName(ticketColour);
                float startX = nodeA.getX() + (NODE_SIZE/2f);
                float startY = nodeA.getY() + (NODE_SIZE/2f);
                float endX = nodeB.getX() + (NODE_SIZE/2f);
                float endY = nodeB.getY() + (NODE_SIZE/2f);
                rootLayout.post(() -> {

                    // Create a LineView to draw the connection
                    LineView lineView = new LineView(context, startX, startY, endX, endY, colour);
                    rootLayout.addView(lineView, 0);
                });
            }
        }
    }

    private int getColourFromName(String colourName) {
        switch (colourName.toLowerCase()){
            case "red": return Color.RED;
            case "blue": return Color.BLUE;
            case "green": return Color.GREEN;
            case "yellow": return Color.YELLOW;

            default: return Color.BLACK;
        }
    }

    private Node getNodeById(int nodeId) {
        for (Node node : nodes) {
            if (node.getNodeNum() == nodeId) {
                return node;
            }
        }
        return null;
    }

    private Node createNode(int index, int x, int y) {
        int nodeNum = index; // NODE number
        String[] acceptedTravelMethods = {"Walk", "Bus", "Train"}; // Example travel methods
        int[] connectedNodes = {}; // Initially no connected nodes
        String[] stationColours = {"Red", "Blue"}; // Example colours

        return new Node(nodeNum, acceptedTravelMethods, connectedNodes, stationColours, x, y);
    }

    private NodeView createNodeView(Node node, int colour) {
        NodeView nodeView = new NodeView(context);
        int circleColour = generateRandomColor();
        nodeView.setNodeData(node.getNodeNum(), colour);
        return nodeView;
    }

    private void positionNodeView(NodeView nodeView, int xPos, int yPos) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(NODE_SIZE, NODE_SIZE);
        params.leftMargin = xPos;
        params.topMargin = yPos;
        nodeView.setLayoutParams(params);
    }

    private int generateRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    // Custom View to represent the connection line
    public class LineView extends View {
        private Paint paint;
        private float startX, startY, endX, endY;

        public LineView(Context context, float startX, float startY, float endX, float endY, int colour) {
            super(context);
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            paint = new Paint();
            paint.setColor(colour);
            paint.setStrokeWidth(10);
            paint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // Draw a line between the nodes
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }
}
