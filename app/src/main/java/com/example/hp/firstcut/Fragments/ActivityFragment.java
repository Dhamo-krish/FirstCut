package com.example.hp.firstcut.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.hp.firstcut.Adapters.DummyAdapter;
import com.example.hp.firstcut.Adapters.ProActItemAdapter;
import com.example.hp.firstcut.Adapters.ProItemAdapter;
import com.example.hp.firstcut.Adapters.Project;
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
    AmazonS3 s3;
    AWSCredentials credentials = new BasicAWSCredentials("AKIAID5UFVAFVQCYVHRA","xdHWdVe7ROxGZXrbcAaegMmXwFAx0buAwC7zDdgq");
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_activity,container,false);
        fab.setVisibility(View.INVISIBLE);
        act_recycler=(RecyclerView)v.findViewById(R.id.act_recycler);
        camera_fab=(FloatingActionButton)v.findViewById(R.id.camera_fab);

        s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        camera_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CameraActivity.class));
            }
        });
        act_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        System.out.println("The project name Is : "+ DummyAdapter.s3path);
        proAdap.add(new ProjectActivityAdapter("MainActivity"));
        proAdap.add(new ProjectActivityAdapter("SecondActivity"));
        setCard();
        new S3_list_activity_Task().execute();


        return v;
    }
    public void setCard(){
        pAIa=new ProActItemAdapter(R.layout.actcard,proAdap);
        act_recycler.setAdapter(pAIa);
    }
    @Override
    public void onResume() {
        super.onResume();

        pAIa.setOnItemClickListener2(new ProActItemAdapter.MyClickListener2() {
            @Override
            public void onItemClick(int position, View v) {
                DummyAdapter.Activity_Name="";
                DummyAdapter.Activity_Name = proAdap.get(position).getActivities();
                Intent intent = new Intent(getActivity(), ImageViewActivity.class);

                startActivity(intent);
            }
        });

    }
    public class S3_list_activity_Task extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);

            String name  = sharedPreferences.getString("name","");


            String s3objpath= "com.jusdraw/"+name+"/";

            System.out.println("Name is : "+name+ s3objpath);

            ListObjectsRequest listobjects = new ListObjectsRequest().withBucketName("com.jusdraw");



            ObjectListing result = s3.listObjects(listobjects);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()){
                System.out.println("The Object Key is : " + objectSummary.getKey().toString());
                String[] split = objectSummary.getKey().toString().split("/");
                System.out.println("the splited Key is : " + split[0]);

                if(split[0].equals(name)){
                    if(split[1].equals(DummyAdapter.s3path)){
                        System.out.println("THe activity name is : "+split[2]);
                        proAdap.add(new ProjectActivityAdapter(split[2]));
                    }

                }






//                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("firstcutapplication/"+)
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCard();
                }
            });
            return null;
        }
    }
}
