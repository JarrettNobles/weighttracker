package com.weighttracker.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom View that draws a simple line graph of weight entries over time.
 * Entries are expected sorted newest-first; this class reverses them for
 * left-to-right chronological plotting.
 */
public class WeightGraphView extends View {

    private List<WeightEntry> entries = new ArrayList<>();

    private Paint linePaint;
    private Paint dotPaint;
    private Paint gridPaint;

    private static final float LINE_WIDTH = 2.5f;
    private static final float DOT_RADIUS = 5f;
    private static final int COLOR_LINE = 0xFFE8533F;   // primary red
    private static final int COLOR_DOT = 0xFFE8533F;
    private static final int COLOR_GRID = 0xFFEEEEEE;

    public WeightGraphView(Context context) {
        super(context);
        init();
    }

    public WeightGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeightGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(COLOR_LINE);
        linePaint.setStrokeWidth(LINE_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setColor(COLOR_DOT);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        gridPaint = new Paint();
        gridPaint.setColor(COLOR_GRID);
        gridPaint.setStrokeWidth(1f);
        gridPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Sets the list of entries to plot. Expects entries sorted newest first.
     */
    public void setEntries(List<WeightEntry> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (entries.size() < 2) {
            // Not enough data to draw a line
            return;
        }

        float padLeft = 20f;
        float padRight = 20f;
        float padTop = 15f;
        float padBottom = 15f;

        float width = getWidth() - padLeft - padRight;
        float height = getHeight() - padTop - padBottom;

        if (width <= 0 || height <= 0) return;

        // Reverse entries for chronological order (oldest on left)
        List<WeightEntry> chronological = new ArrayList<>(entries);
        Collections.reverse(chronological);

        // Find min and max weight for scaling
        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;
        for (WeightEntry entry : chronological) {
            minWeight = Math.min(minWeight, entry.getWeight());
            maxWeight = Math.max(maxWeight, entry.getWeight());
        }

        // Add a small padding to the range so points aren't on the edge
        double range = maxWeight - minWeight;
        if (range == 0) range = 1; // avoid division by zero for flat line
        double paddedMin = minWeight - range * 0.15;
        double paddedMax = maxWeight + range * 0.15;
        double paddedRange = paddedMax - paddedMin;

        // Draw horizontal grid lines (3 lines)
        for (int i = 0; i <= 2; i++) {
            float y = padTop + (height / 2f) * i;
            canvas.drawLine(padLeft, y, padLeft + width, y, gridPaint);
        }

        // Calculate points
        int pointCount = chronological.size();
        float[] xPoints = new float[pointCount];
        float[] yPoints = new float[pointCount];

        for (int i = 0; i < pointCount; i++) {
            double weight = chronological.get(i).getWeight();
            xPoints[i] = padLeft + (width * i) / (pointCount - 1);
            yPoints[i] = padTop + height * (1.0f - (float)((weight - paddedMin) / paddedRange));
        }

        // Draw line path
        Path path = new Path();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < pointCount; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }
        canvas.drawPath(path, linePaint);

        // Draw dots at each data point
        for (int i = 0; i < pointCount; i++) {
            canvas.drawCircle(xPoints[i], yPoints[i], DOT_RADIUS, dotPaint);
        }
    }
}
