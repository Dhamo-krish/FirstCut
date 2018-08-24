package com.example.hp.firstcut.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.example.hp.firstcut.Adapters.DummyAdapter;
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
    AmazonS3 s3;
    public static boolean ischanged = false;
    CognitoCachingCredentialsProvider credentialsProvider;
    Thread thread;

    AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3QRFSJLAJP5U3GA","JnttF8Wooim3B5n+SrKnzeH/47GEUykKf+bYRmkz");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_project,container,false);
        fab.setVisibility(View.VISIBLE);
        projects.clear();


//        credentialsProvider = new CognitoCachingCredentialsProvider(
//                getActivity(),
//                "us-west-2:07122afa-1165-4d97-bbb4-a6d1de6aefb8", // Identity pool ID
//                Regions.US_WEST_2 // Region
//        );

        s3 = new AmazonS3Client(credentials);
       s3.setRegion(Region.getRegion(Regions.US_EAST_1));


        pro_recycler=(RecyclerView)v.findViewById(R.id.pro_recycler);
        pro_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        projects.add(new Project("FirstApp"));
        projects.add(new Project("Whatsapp"));
        projects.add(new Project("FaceBook"));

        new List_ProjectNames_Task().execute();


//        thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                pIa=new ProItemAdapter(R.layout.procard,projects);
//                pro_recycler.setAdapter(pIa);
//            }
//        };
//        thread.run();
       setCard();

        return v;
    }

    public void setCard(){
        pIa=new ProItemAdapter(R.layout.procard,projects);
        pro_recycler.setAdapter(pIa);

    }

    @Override
    public void onResume() {
        super.onResume();


        pIa.setOnItemClickListener(new ProItemAdapter.MyClickListener() {
            @Override
            public void onItemClick(int ff, View v) {


                //startActivity(new Intent(getActivity(),CameraActivity.class));
                DummyAdapter.s3path = "";
                DummyAdapter.s3path = projects.get(ff).getPro_name();

                getFragmentManager().beginTransaction().replace(R.id.frame_container,new ActivityFragment()).addToBackStack(null).commit();

            }
        });
    }
    public class List_ProjectNames_Task extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);

            String name  = sharedPreferences.getString("name","");


            String s3objpath= "com.jusdraw/"+name+"/";

            System.out.println("Name is : "+name+ s3objpath);

            ListObjectsRequest listobjects = new ListObjectsRequest().withBucketName("com.jusdraw");
            String previous = null;
            int a = 1;


            ObjectListing result = s3.listObjects(listobjects);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()){

                 System.out.println("The Object Key is : " + objectSummary.getKey().toString());
                 String[] split = objectSummary.getKey().toString().split("/");
                 System.out.println("the splited Key is : " + split[0]);
                if(a==1){
                   previous = split[1];
                    if(split[0].equals(name)) {
                        projects.add(new Project(split[1]));
                    }
                   a=0;
                }

                if(!previous.equals(split[1])){
                    if(split[0].equals(name)){
                        projects.add(new Project(split[1]));
                        previous=split[0];
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
