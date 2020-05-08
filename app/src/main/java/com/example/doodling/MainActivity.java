package com.example.doodling;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.doodling.View.MyAdapter;
import com.example.doodling.paintType.ActionType;
import com.example.doodling.View.Drawing;
import com.example.doodling.View.DrawingView;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.type)
    Button type;
    @BindView(R.id.draw_btn)
    ImageButton pen;
    @BindView(R.id.color)
    ImageButton color;
    @BindView(R.id.erase_btn)
    ImageButton erase;
    @BindView(R.id.reset)
    ImageButton clear;
    @BindView(R.id.save)
    ImageButton save;
    @BindView(R.id.drawingView)
    DrawingView drawingView;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.left_view)
    RelativeLayout left_view;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.twinklingRefreshLayout)
    TwinklingRefreshLayout mTkrefreshlayout;
    private static final String TAG="MainActivity";
    private DataBase dataBase;
    private Drawing drawing;
    private String name;
    private List<Drawing> drawings=new ArrayList<>();
    private AlertDialog ColorDialog;
    private AlertDialog PaintDialog;
    private AlertDialog ShapeDialog;
    private AlertDialog saveImageDialog;
    private MyAdapter adapter;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        drawingView.setStoke(dip2px(5));
        drawing=new Drawing();
        dataBase=new DataBase(this);
        update();
        initData();
    }

    private void update() {
        drawings=dataBase.getDrawing();
        Collections.reverse(drawings);
        adapter=new MyAdapter(this,drawings);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
//        adapter.setOnItemDeleteClickListener(new MyAdapter.onItemDeleteListener() {
//            @Override
//            public void onDeleteClick(int i) {
//                drawings.remove(i);
//                adapter.notifyDataSetChanged();
//            }
//        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                drawing=drawings.get(i);
                dataBase.getMap(drawing);
                Drawable drawable=drawing.getDrawable();
                drawingView.setBackground(drawable);
                //Bitmap bitmap=drawingView.buildBitmap();
                Bitmap bitmap = drawingView.getDrawingCache();
                drawingView.BitmapToBytes(bitmap);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return drawingView.onTouchEvent(event);
    }

    @OnClick({R.id.type,R.id.color,R.id.erase_btn,R.id.draw_btn,R.id.reset,R.id.save,R.id.search,R.id.left_view})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.type:
                showShapeDialog();
                break;
            case R.id.color:
                showColorDialog();
                break;
            case R.id.draw_btn:
                drawingView.setErase(false);
                showSizeDialog();
                break;
            case R.id.reset:
                drawingView.reset();
                drawingView.setBackground(null);
                break;
            case R.id.erase_btn:
                drawingView.setType(ActionType.Path);
                drawingView.setErase(true);
                //drawingView.setStoke(dip2px(15));
                break;
            case R.id.save:
                saveImage();
                break;
            case R.id.search:
                Search();
                break;
        }
    }

    public void Search() {
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    View view = getWindow().peekDecorView();
                    if (null != view) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    String editText =search.getText().toString().trim();
                    //dataBase = new DataBase(getApplicationContext());
                    SQLiteDatabase db = dataBase.getReadableDatabase();
                    cursor=db.rawQuery("SELECT * FROM DRAWING WHERE name like '%"+ editText+"%'",null );
                    List<Drawing> mData=new ArrayList<>();
                    if(cursor.moveToFirst()){
                        if(cursor.moveToFirst()){
                            do{
                                Drawing draw=new Drawing();
                                draw.setId(cursor.getInt(0));
                                draw.setName(cursor.getString(1));
                                draw.setDate(cursor.getString(2));
                                mData.add(draw);
                            }while (cursor.moveToNext());{
                            }
                        }
                    }
                    drawings.clear();
                    drawings.addAll(mData);
                    adapter.notifyDataSetChanged();

                }

                return false;
            }
        });
    }
    private void showShapeDialog(){
        if(ShapeDialog==null){
            ShapeDialog=new AlertDialog.Builder(this)
                    .setTitle("请选择形状")
                    .setSingleChoiceItems(new String[]{"路径", "直线", "矩形", "圆形"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            drawingView.setType(ActionType.Path);
                                            break;
                                        case 1:
                                            drawingView.setType(ActionType.Line);
                                            break;
                                        case 2:
                                            drawingView.setType(ActionType.Rect);
                                            break;
                                        case 3:
                                            drawingView.setType(ActionType.Circle);
                                            break;
                                            default:
                                                break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        ShapeDialog.show();
    }

    private void showColorDialog(){

        if(ColorDialog==null){
            ColorDialog=new AlertDialog.Builder(this)
                    .setTitle("请选择颜色")
                    .setSingleChoiceItems(new String[]{"黑色", "红色", "蓝色", "黄色"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            drawingView.setColor("#000000");
                                            break;
                                        case 1:
                                            drawingView.setColor("#ff0000");
                                            break;
                                        case 2:
                                            drawingView.setColor("#0000ff");
                                            break;
                                        case 3:
                                            drawingView.setColor("#ffff00");
                                            break;
                                            default:
                                                break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
                    }
        ColorDialog.show();
    }

    private void showSizeDialog() {
        if(PaintDialog==null){
            PaintDialog=new AlertDialog.Builder(this)
                    .setTitle("请选择画笔大小")
                    .setSingleChoiceItems(new String[]{"小", "中", "大", "超大"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            drawingView.setStoke(dip2px(5));
                                            break;
                                        case 1:
                                            drawingView.setStoke(dip2px(10));
                                            break;
                                        case 2:
                                            drawingView.setStoke(dip2px(15));
                                            break;
                                        case 3:
                                            drawingView.setStoke(dip2px(20));
                                            break;
                                            default:
                                                break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();

        }
        PaintDialog.show();
    }

    private void saveImage() {
        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");
        final Date date=new Date(System.currentTimeMillis());
        final Bitmap bitmap=drawingView.getBitmap();
        final EditText editText = new EditText(this);
        if (saveImageDialog == null) {
            saveImageDialog = new AlertDialog.Builder(this)
                    .setTitle("请设置图片名称")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            name =editText.getText().toString() ;
                            if(name.isEmpty()){
                                showMessage("图片名字不能为空！");
                            }else {

                                //String Name=name+"_"+simpleDateFormat.format(date);
                                drawing.setName(name);
                                drawing.setDate(simpleDateFormat.format(date));
                                dataBase.addDrawing(drawing,drawingView.BitmapToBytes(bitmap));
//                                if(saveImageByPNG(drawingView.getBitmap())){
                                    showMessage("保存成功！");
                                    drawingView.reset();
                                    update();
                            }
                        }
                    }).setNegativeButton("取消", null)
                    .create();

        }
        saveImageDialog.show();
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
//保存图片
    public static boolean saveImageByPNG( Bitmap bitmap){
        String Path= Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/drawingView/" + System.currentTimeMillis() + ".png";
        if (!new File(Path).exists()) {
            new File(Path).getParentFile().mkdir();
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(Path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private int dip2px(float dpValue){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
