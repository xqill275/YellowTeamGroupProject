package com.example.phoneclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

// This class represents a custom view for displaying a NODE as a circle with its number in the centre.
public class NodeView extends View {

    // The unique number assigned to the node
    private int nodeNum;

    // Paint object for drawing the circle
    private Paint circlePaint;

    // Constructor for programmatic use (when the view is created directly in code)
    public NodeView(Context context) {
        super(context);
        init(); // Initialises the Paint object
    }

    // Constructor for inflating the view from XML
    public NodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(); // Initialises the Paint object
    }

    // Constructor with additional style attributes
    public NodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(); // Initialises the Paint object
    }

    // Initialises the Paint object used to draw the circle
    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(0xFF2196F3); // Default blue colour
        circlePaint.setStyle(Paint.Style.FILL); // Fill the circle
    }

    // Sets the node data (node number and colour) and requests a redraw
    public void setNodeData(int nodeNum, int colour) {
        this.nodeNum = nodeNum; // Assign the node number
        circlePaint.setColor(colour); // Update the circle's colour
        invalidate(); // Request the view to be redrawn
    }

    // Called when the view needs to be drawn on the screen
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the circle's radius and centre
        float radius = Math.min(getWidth(), getHeight()) / 3f; // Radius is one-third of the smaller dimension
        float centreX = getWidth() / 2f; // Centre X-coordinate
        float centreY = getHeight() / 2f; // Centre Y-coordinate

        // Draw the circle
        canvas.drawCircle(centreX, centreY, radius, circlePaint);

        // Draw the node number as text in the centre of the circle
        Paint textPaint = new Paint();
        textPaint.setColor(0xFFFFFFFF); // White colour for the text
        textPaint.setTextSize(50); // Font size
        textPaint.setTextAlign(Paint.Align.CENTER); // Align text to the centre
        canvas.drawText(String.valueOf(nodeNum), centreX, centreY + 15, textPaint); // Draw the number
    }
}