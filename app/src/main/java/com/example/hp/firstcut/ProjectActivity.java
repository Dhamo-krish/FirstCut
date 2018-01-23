package com.example.hp.firstcut;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.hp.firstcut.Adapters.ProItemAdapter;
import com.example.hp.firstcut.Adapters.Project;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {
    RecyclerView pro_recycler;
    ArrayList<Project> projects=new ArrayList<>();
    ProItemAdapter pIa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Project");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        pro_recycler=(RecyclerView)findViewById(R.id.pro_recycler);
        pro_recycler.setLayoutManager(new LinearLayoutManager(ProjectActivity.this));
        projects.add(new Project("FirstApp"));
        projects.add(new Project("Whatsapp"));
        projects.add(new Project("FaceBook"));
        pIa=new ProItemAdapter(R.layout.procard,projects);
        pro_recycler.setAdapter(pIa);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BackFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pIa.setOnItemClickListener(new ProItemAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startActivity(new Intent(ProjectActivity.this,CameraActivity.class));
            }
        });
    }

}
