package com.itclanbd.icnotificationblocker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> APP_NAME = new ArrayList<>();
    private RecyclerView recyclerView;
    private App_List_Adapter mAdapter;
    Switch block_all_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            block_all_switch = findViewById(R.id.block_all_switch);
            recyclerView = findViewById(R.id.app_list_recycler);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new App_List_Adapter(MainActivity.this, new ApkInfoExtractor(MainActivity.this).GetAllInstalledApkInfo());
            recyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }
}
