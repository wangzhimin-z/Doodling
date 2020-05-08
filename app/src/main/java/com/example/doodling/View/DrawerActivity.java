//package com.example.doodling.View;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.EditText;
//import android.widget.ListView;
//
//import com.example.doodling.DataBase;
//import com.example.doodling.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DrawerActivity extends AppCompatActivity {
//
//    private EditText search;
//    private ListView listView;
//    private List<Drawing> drawings=new ArrayList<>();
//    private DataBase dataBase;
//    private MyAdapter adapter;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_left);
//        search=(EditText)findViewById(R.id.search);
//        Search();
//        dataBase=new DataBase(this);
//        listView=(ListView)findViewById(R.id.listView);
//        drawings=dataBase.getDrawing();
//        adapter=new MyAdapter(this,drawings);
//        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        adapter.setOnItemDeleteClickListener(new MyAdapter.onItemDeleteListener() {
//            @Override
//            public void onDeleteClick(int i) {
//                drawings.remove(i);
//                dataBase.deleteDrawing(drawings.get(i));
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    public void Search() {
//    }
//}
