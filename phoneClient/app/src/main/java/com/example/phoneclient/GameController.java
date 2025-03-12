package com.example.phoneclient;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// The GameController manages the creation and display of nodes
public class GameController {
    private Context context;
    private FrameLayout rootLayout; // Layout to hold the nodes
    private FrameLayout uiLayer;
    private static final int NODE_SIZE = 200; // Size of each node view in pixels
    private List<Node> nodes; // List to hold NODE objects
    private static final String BASE_MAP_URL = "http://trinity-developments.co.uk/maps/";
    private static final String BASE_GAME_URL = "http://trinity-developments.co.uk/games/";
    private static final String BASE_PLAYER_URL = "http://trinity-developments.co.uk/players/";
    private static final String TAG = "GameController";
    private OkHttpClient client = new OkHttpClient();
    private int gameID;
    private HashMap<Integer, View> playerMarkers = new HashMap<>();

    private int screenWidth;
    private int screenHeight;

    private int mapWidth;
    private int mapHeight;

    private int hostStartLocation;
    private int playerId;
    private int hostID;


    private String selectedTicket = null;

    // Constructor that accepts the context and root layout
    public GameController(Context context, FrameLayout rootLayout, FrameLayout uiLayer, int gameID, int hostStartLocation, int playerId, int hostID) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.uiLayer = uiLayer;
        this.nodes = new ArrayList<>();
        this.gameID = gameID;
        this.hostStartLocation = hostStartLocation;
        this.playerId = playerId;
        this.hostID = hostID;
        Log.d(TAG, "host Start Location: " + hostStartLocation);
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
        getMapData(mapId, () -> {
            updatePlayerPositions(gameID);
            fetchAndDisplayPlayerTickets(playerId);
        });
    }

    private void fetchAndDisplayPlayerTickets(int playerId) {
        String url = BASE_PLAYER_URL + playerId;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch player details", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected response: " + response);
                    return;
                }

                String responseData = response.body().string();
                Log.d(TAG, "Player Details Response: " + responseData);

                try {
                    JSONObject playerObject = new JSONObject(responseData);
                    int yellowTickets = playerObject.optInt("yellow", 0);
                    int greenTickets = playerObject.optInt("green", 0);
                    int redTickets = playerObject.optInt("red", 0);
                    int blackTickets = playerObject.optInt("black", 0);
                    int doubleTickets = playerObject.optInt("2x", 0);

                    // Run UI updates on the main thread
                    rootLayout.post(() -> {
                        addShowTicketsButton(); // Ensure button is always above
                        displayTicketButtons(yellowTickets, greenTickets, redTickets, blackTickets, doubleTickets);
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing player details", e);
                }
            }
        });
    }

    private void addShowTicketsButton() {
        Button showTicketsButton = new Button(context);
        showTicketsButton.setText("Show Tickets");

        // Button styling
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.topMargin = 50;  // Position at top
        params.leftMargin = 50; // Left-aligned

        showTicketsButton.setLayoutParams(params);

        showTicketsButton.setOnClickListener(v -> {
            // Toggle ticket visibility
            for (int i = 0; i < rootLayout.getChildCount(); i++) {
                View child = rootLayout.getChildAt(i);
                if (child instanceof Button) {
                    child.setVisibility(child.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            }
        });

        uiLayer.addView(showTicketsButton); // Add to UI Layer (always on top)
    }

    private void displayTicketButtons(int yellow, int green, int red, int black, int doubleX) {
        //rootLayout.removeAllViews(); // Clear existing buttons before adding new ones

        Button toggleButton = new Button(context);
        toggleButton.setText("Show Tickets");
        toggleButton.setBackgroundColor(Color.LTGRAY);

        FrameLayout.LayoutParams toggleParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        toggleParams.leftMargin = 20;
        toggleParams.topMargin = 50;
        toggleButton.setLayoutParams(toggleParams);
        rootLayout.addView(toggleButton);

        // Container for ticket buttons
        FrameLayout ticketContainer = new FrameLayout(context);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        containerParams.leftMargin = 20;
        containerParams.topMargin = 150; // Place below the toggle button
        ticketContainer.setLayoutParams(containerParams);
        rootLayout.addView(ticketContainer);

        List<Button> ticketButtons = new ArrayList<>();
        int buttonSpacing = 150;
        int buttonIndex = 0;

        if (yellow > 0) ticketButtons.add(createTicketButton("Yellow", yellow, Color.YELLOW, buttonIndex++, buttonSpacing));
        if (green > 0) ticketButtons.add(createTicketButton("Green", green, Color.GREEN, buttonIndex++, buttonSpacing));
        if (red > 0) ticketButtons.add(createTicketButton("Red", red, Color.RED, buttonIndex++, buttonSpacing));
        if (black > 0) ticketButtons.add(createTicketButton("Black", black, Color.BLACK, buttonIndex++, buttonSpacing));
        if (doubleX > 0) ticketButtons.add(createTicketButton("2X", doubleX, Color.GRAY, buttonIndex++, buttonSpacing));

        // Initially hide ticket buttons
        for (Button button : ticketButtons) {
            button.setVisibility(View.GONE);
            ticketContainer.addView(button);
        }

        toggleButton.setOnClickListener(v -> {
            boolean isVisible = ticketButtons.get(0).getVisibility() == View.VISIBLE;
            for (Button button : ticketButtons) {
                button.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            }
        });
    }

    private Button createTicketButton(String label, int count, int color, int index, int spacing) {
        Button button = new Button(context);
        button.setText(label + ": " + count);
        button.setBackgroundColor(color);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = index * spacing;
        button.setLayoutParams(params);

        button.setOnClickListener(v -> selectTicket(label));

        return button;
    }

    private void selectTicket(String ticket) {
        selectedTicket = ticket.toLowerCase();
        Log.d(TAG, "Selected ticket: " + selectedTicket);
    }

    private void updatePlayerPositions(int gameId) {
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
                    JSONArray playersArray = gameObject.getJSONArray("players");

                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject playerObject = playersArray.getJSONObject(i);
                        int playerId = playerObject.getInt("playerId");
                        String colorName = playerObject.getString("colour");
                        String location = playerObject.getString("location");

                        int locationId;
                        if (location.equals("Hidden")) {
                            if (hostStartLocation == -1) {
                                continue; // Skip if hostStartLocation is not available
                            }
                            locationId = hostStartLocation; // Show host's start location
                        } else {
                            locationId = Integer.parseInt(location);
                        }

                        Node node = getNodeById(locationId);
                        if (node == null) continue;

                        int color = getColourFromName(colorName);
                        int x = node.getX() + 50;
                        int y = node.getY() + 50;

                        rootLayout.post(() -> addOrUpdatePlayerMarker(playerId, x, y, color));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing player positions", e);
                }
            }
        });

        rootLayout.postDelayed(() -> updatePlayerPositions(gameID), 2000);
    }

    private void addOrUpdatePlayerMarker(int playerId, int x, int y, int color) {
        View marker = playerMarkers.get(playerId);
        if (marker == null) {
            marker = new View(context);
            marker.setBackgroundColor(color);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(50, 50);
            params.leftMargin = x;
            params.topMargin = y;
            marker.setLayoutParams(params);
            playerMarkers.put(playerId, marker);
            rootLayout.addView(marker);
        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) marker.getLayoutParams();
            params.leftMargin = x;
            params.topMargin = y;
            marker.setLayoutParams(params);
        }
    }


    private void getMapData(int mapId, Runnable onComplete) {
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
                Log.d(TAG, "Map Data Response: " + responseData);
                if (responseData.isEmpty()) {
                    Log.e(TAG, "Empty map data received!");
                }
                parseMapData(responseData);

                // Ensure UI updates are done on the main thread
                rootLayout.post(onComplete);
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
                Log.d(TAG, "Adding node at: X=" + scaledX + " Y=" + scaledY);
                Node node = createNode(nodeNum, scaledX, scaledY);
                nodes.add(node);


                NodeView nodeView = createNodeView(node, colour);
                positionNodeView(nodeView, scaledX, scaledY);
                Log.d(TAG, "Total Nodes Parsed: " + nodes.size());
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
            case "clear": return Color.WHITE;

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
        nodeView.setNodeData(node.getNodeNum(), colour);

        nodeView.setOnClickListener(v -> {
            if (selectedTicket != null) {
               int destination = node.getNodeNum();
                Log.d(TAG, "trying to make move: destination: "+destination+ " ticket: "+selectedTicket);
               makeMove(playerId, destination, selectedTicket);
            } else {
                Log.d(TAG, "No ticket selected!");
            }
        });

        return nodeView;
    }

    private void makeMove(int playerId, int destination, String ticketType) {
        Log.d(TAG, "Attempting to move");
        OkHttpClient client = new OkHttpClient();

        // Replace with your actual server endpoint
        String url = "http://trinity-developments.co.uk/players/" + playerId + "/moves";

        // Create JSON payload
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gameID", gameID);
            jsonObject.put("ticket", ticketType);
            jsonObject.put("destination", destination);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Log.d(TAG, "move request: "+body);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    Log.d(TAG, "Before updating, hostStartLocation: " + hostStartLocation);
                    if (hostID > 0){
                        hostStartLocation = destination;
                    }
                    Log.d(TAG, "After updating, hostStartLocation: " + hostStartLocation);
                } else {
                    Log.e(TAG, "Move failed. Code: " + response.code() + " Response: " + responseBody);
                }
            }
        });
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
