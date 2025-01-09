package com.example.phoneclient;


public class Node {

    private int nodeNum;

    // Array of travel methods accepted by this node (e.g., bus, train)
    private String[] acceptedTravelMethods;

    // Array of node IDs that are directly connected to this node
    private int[] connectedNodes;

    // Array of station colours associated with this node (e.g., line colors)
    private String[] stationColours;

    // Constructor to initialise the node with its attributes
    public Node(int nodeNum, String[] acceptedTravelMethods, int[] connectedNodes, String[] stationColours) {
        this.nodeNum = nodeNum; // Set the node number
        this.acceptedTravelMethods = acceptedTravelMethods; // Set the accepted travel methods
        this.connectedNodes = connectedNodes; // Set the connected nodes
        this.stationColours = stationColours; // Set the station colors
    }


    public int getNodeNum() {
        return nodeNum;
    }


    public String[] getAcceptedTravelMethods() {
        return acceptedTravelMethods;
    }


    public int[] getConnectedNodes() {
        return connectedNodes;
    }


    public String[] getStationColours() {
        return stationColours;
    }


    public void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }


    public void setAcceptedTravelMethods(String[] acceptedTravelMethods) {
        this.acceptedTravelMethods = acceptedTravelMethods;
    }


    public void setConnectedNodes(int[] connectedNodes) {
        this.connectedNodes = connectedNodes;
    }


    public void setStationColours(String[] stationColours) {
        this.stationColours = stationColours;
    }
}
