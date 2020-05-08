package com.example.doodling.paintType;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LinePaint extends BasePaint{

    public LinePaint(float x, float y, int strokeWidth, int paintColor) {
        super(x, y, strokeWidth, paintColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(paintColor);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    @Override
    public void onMove(float moveX, float moveY) {
        this.stopX = moveX;
        this.stopY = moveY;
    }
}
