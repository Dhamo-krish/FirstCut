package com.example.hp.firstcut.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hp.firstcut.Adapters.ProItemAdapter;
import com.example.hp.firstcut.Adapters.Project;
import com.example.hp.firstcut.CameraActivity;
import com.example.hp.firstcut.ProjectActivity;
import com.example.hp.firstcut.R;

import java.util.ArrayList;

import static com.example.hp.firstcut.ProjectActivity.fab;

/**
 * Created by HP on 1/24/2018.
 */

public class ProjectFragment extends Fragment {
    RecyclerView pro_recycler;
    ArrayList<Project> projects=new ArrayList<>();
    ProItemAdapter pIa;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_project,container,false);
        fab.setVisibility(View.VISIBLE);
        projects.clear();
        pro_recycler=(RecyclerView)v.findViewById(R.id.pro_recycler);
        pro_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        projects.add(new Project("FirstApp"));
        projects.add(new Project("Whatsapp"));
        projects.add(new Project("FaceBook"));
        pIa=new ProItemAdapter(R.layout.procard,projects);
        pro_recycler.setAdapter(pIa);
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        pIa.setOnItemClickListener(new ProItemAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //startActivity(new Intent(getActivity(),CameraActivity.class));
                getFragmentManager().beginTransaction().replace(R.id.frame_container,new ActivityFragment()).addToBackStack(null).commit();

            }
        });
    }

}
