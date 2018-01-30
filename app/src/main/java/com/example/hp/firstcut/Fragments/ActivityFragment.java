package com.example.hp.firstcut.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hp.firstcut.Adapters.ProActItemAdapter;
import com.example.hp.firstcut.Adapters.ProItemAdapter;
import com.example.hp.firstcut.Adapters.ProjectActivityAdapter;
import com.example.hp.firstcut.CameraActivity;
import com.example.hp.firstcut.ImageViewActivity;
import com.example.hp.firstcut.R;

import java.util.ArrayList;

import static com.example.hp.firstcut.ProjectActivity.fab;

/**
 * Created by HP on 1/24/2018.
 */

public class ActivityFragment extends Fragment {
    RecyclerView act_recycler;
    ArrayList<ProjectActivityAdapter> proAdap=new ArrayList<>();
    ProActItemAdapter pAIa;
    FloatingActionButton camera_fab;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_activity,container,false);
        fab.setVisibility(View.INVISIBLE);
        act_recycler=(RecyclerView)v.findViewById(R.id.act_recycler);
        camera_fab=(FloatingActionButton)v.findViewById(R.id.camera_fab);
        camera_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CameraActivity.class));
            }
        });
        act_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        proAdap.add(new ProjectActivityAdapter("MainActivity","//file"));
        proAdap.add(new ProjectActivityAdapter("SecondActivity",""));
        pAIa=new ProActItemAdapter(R.layout.procard,proAdap);
        act_recycler.setAdapter(pAIa);
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        pAIa.setOnItemClickListener2(new ProActItemAdapter.MyClickListener2()
        {


            @Override
            public void onItemClick(int position, View v) {
                Intent imageIntent =new Intent(getActivity(), ImageViewActivity.class);
                imageIntent.putExtra("key",proAdap.get(position).getUrl()) ;
                if(proAdap.get(position).getUrl().equals("")) {
                    Toast.makeText(getActivity(), "NO Image Exists", Toast.LENGTH_SHORT).show();
                }
                else {startActivity(imageIntent);}

            }



        });
    }
}
