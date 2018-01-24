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
import com.example.hp.firstcut.Fragments.ProjectFragment;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {

    ProjectFragment projectFragment;
    public static FloatingActionButton fab;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Project");

         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BackFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.frame_container,new ProjectFragment()).commit();
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//       projectFragment.setOnItemClickListener2(new ProjectFragment.MyClickListener3() {
//           @Override
//           public void onItemClick(int position, View v) {
//               fab.setVisibility(View.INVISIBLE);
//           }
//       });
//    }



}
