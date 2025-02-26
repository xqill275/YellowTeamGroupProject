package com.example.phoneclient;

public class Node {
    private int nodeNum;
    private String[] acceptedTravelMethods;
    private int[] connectedNodes;
    private String[] stationColours;
    private int x;
    private int y;

    public Node(int nodeNum, String[] acceptedTravelMethods, int[] connectedNodes, String[] stationColours, int x, int y) {
        this.nodeNum = nodeNum;
        this.acceptedTravelMethods = acceptedTravelMethods;
        this.connectedNodes = connectedNodes;
        this.stationColours = stationColours;
        this.x = x;
        this.y = y;
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
