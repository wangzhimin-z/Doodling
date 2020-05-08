package com.example.doodling.paintType;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RectPaint extends BasePaint{

    public RectPaint(float x, float y, int strokeWidth, int paintColor) {
        super(x, y, strokeWidth, paintColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(paintColor);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawRect(startX, startY, stopX, stopY, paint);
    }

    @Override
    public void onMove(float moveX, float moveY) {
        stopX = moveX;
        stopY = moveY;
    }
}
