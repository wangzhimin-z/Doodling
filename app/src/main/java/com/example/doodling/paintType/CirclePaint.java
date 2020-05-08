package com.example.doodling.paintType;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CirclePaint extends BasePaint{


    /**
     * 半径
     */
    private float radius;

    public CirclePaint(float x, float y, int strokeWidth, int paintColor) {
        super(x, y, strokeWidth, paintColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(paintColor);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius, paint);
    }

    @Override
    public void onMove(float moveX, float moveY) {
        this.stopX = moveX;
        this.stopY = moveY;
        this.radius = (float) ((Math.sqrt((moveX - startX) * (moveX - startX)
                + (moveY - startY) * (moveY - startY))) / 2);
    }
}
