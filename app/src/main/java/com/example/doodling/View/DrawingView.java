package com.example.doodling.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.doodling.paintType.ActionType;
import com.example.doodling.paintType.BasePaint;
import com.example.doodling.paintType.CirclePaint;
import com.example.doodling.paintType.LinePaint;
import com.example.doodling.paintType.PathPaint;
import com.example.doodling.paintType.RectPaint;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder = null;
    private Paint paint;//画笔
    // 当前所选画笔的形状
    private BasePaint mPaint = null;
    private int paintColor=Color.BLACK;//画笔颜色
    private int paintStroke=5;//画笔大小
    private boolean erase=false;//橡皮擦
    private Bitmap bitmap;//位图
    private List<BasePaint> mBasePaint;//画笔集合
    private ActionType mActionType = ActionType.Path;

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        initPaint();
    }

    private void init() {
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        this.setFocusable(true);
    }


    private void initPaint() {
        paint=new Paint();
        paint.setColor(paintColor);
        paint.setStrokeWidth(paintStroke);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        surfaceHolder.unlockCanvasAndPost(canvas);
        mBasePaint = new ArrayList<>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }
        int x=(int)event.getX();
        int y=(int)event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                setAction(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas canvas=surfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                for(BasePaint basePaint: mBasePaint){
                    basePaint.onDraw(canvas);
                }
                mPaint.onMove(x,y);
                mPaint.onDraw(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                break;
            case MotionEvent.ACTION_UP:
                mBasePaint.add(mPaint);
                mPaint=null;
                break;

                default:
                    break;
        }
        return true;
    }

    /**
     * 设置画笔颜色
     * @param color
     */
    public void setColor(String color){
        this.paintColor=Color.parseColor(color);
        paint.setColor(paintColor);

    }

    /**
     * 设置画笔大小
     * @param stoke
     */
    public void setStoke(int stoke){
        this.paintStroke=stoke;
    }

    /**
     * 画笔形状
     * @param type
     */
    public void setType(ActionType type){
        this.mActionType=type;
    }

    public void setErase(boolean mErase){
        this.erase=mErase;
        if(erase){
            int mColor=Color.WHITE;
            paintColor=mColor;
        }else {
            paintColor=paint.getColor();
            paint.setXfermode(null);
        }
    }

    public Bitmap buildBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap result = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        return result;
    }
    //获取当前涂鸦画
    public Bitmap getBitmap(){
        bitmap=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        for(BasePaint action:mBasePaint){
            action.onDraw(canvas);
        }
        canvas.drawBitmap(bitmap,0,0,paint);
        BitmapToBytes(bitmap);
        return bitmap;
    }

    public byte[] BitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }



    /**
     * 得到当前画笔的类型，并进行实例化
     *
     * @param x
     * @param y
     */
    private void setAction(float x, float y) {
        switch (mActionType) {
            case Path:
                mPaint = new PathPaint(x, y, paintStroke, paintColor);
                break;
            case Line:
                mPaint = new LinePaint(x, y, paintStroke, paintColor);
                break;
            case Rect:
                mPaint = new RectPaint(x, y, paintStroke, paintColor);
                break;
            case Circle:
                mPaint = new CirclePaint(x, y, paintStroke, paintColor);
                break;
            default:
                break;
        }
    }

    public void reset(){
        if(mBasePaint!=null&& mBasePaint.size()>0){
            mBasePaint.clear();
            Canvas canvas=surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for(BasePaint action:mBasePaint){
                action.onDraw(canvas);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

}
