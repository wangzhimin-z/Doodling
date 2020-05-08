package com.example.doodling.paintType;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class PathPaint extends BasePaint{

    private Path path;

    public PathPaint(float x, float y, int strokeWidth, int paintColor) {
        super(x, y, strokeWidth, paintColor);
        this.path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, y);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(paintColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(path, paint);
    }

    @Override
    public void onMove(float moveX, float moveY) {
        path.lineTo(moveX, moveY);
    }
}
