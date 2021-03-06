package com.example.hp.firstcut;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.RecoverySystem;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.example.hp.firstcut.Fragments.ActivityFragment;
import com.example.hp.firstcut.Fragments.ProjectFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.example.hp.firstcut.Fragments.ProjectFragment.ischanged;

/**
 * Created by HP on 1/23/2018.
 */

public class BackFragment extends BottomSheetDialogFragment {

    TextInputEditText pname;
    Button create;

    AmazonS3 s3;
    SharedPreferences sharedPreferences;
    S3Object s3Object;
    TransferUtility transferUtility;
    Dialog ExDialog;
    View ex;
    PutObjectRequest putObjectRequest;
    public static String Project_Name ;
    ProgressDialog progressDialog;

    AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3QRFSJLAJP5U3GA","JnttF8Wooim3B5n+SrKnzeH/47GEUykKf+bYRmkz");
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);


    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.backfragment, null);
        dialog.setContentView(contentView);
        ExDialog = dialog;
        sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);

        pname = (TextInputEditText)contentView.findViewById(R.id.project_name);
        create = (Button)contentView.findViewById(R.id.project_create);



        s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));



        transferUtility = new TransferUtility(s3,contentView.getContext());

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pname.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter Project Name", Toast.LENGTH_SHORT).show();
                }else{
                    Project_Name = pname.getText().toString().replace(" ","");
                    Toast.makeText(getContext(),pname.getText().toString(), Toast.LENGTH_SHORT).show();
                    new S3_Upload_Task().execute();



                }

                //getFragmentManager().beginTransaction().replace(R.id.frame_container,new ProjectFragment()).addToBackStack(null).commit();
            }
        });


    }
    public void closeDialog(){

        ExDialog.dismiss();

    }
    public class S3_Upload_Task extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Creating Project.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            String name = sharedPreferences.getString("name","");
            File file = new File(Environment.getExternalStorageDirectory()+"/FirstCut/"+ Project_Name);
            Boolean DirectoryCreation = file.mkdirs();
            File newfile= new File(file.getPath()+"/abs.txt");
            if(DirectoryCreation){
                System.out.println("Directory Created");
                try {
                    FileWriter writer = new FileWriter(newfile);
                    writer.write("This is the temp file created for "+Project_Name);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("File Creation"+file.getPath());
            putObjectRequest = new PutObjectRequest("com.jusdraw/"+name+"/"+ Project_Name,Project_Name+".txt",newfile);

            PutObjectResult result = s3.putObject(putObjectRequest);
            System.out.println("Bowwwwww"+result);

            try {
                Thread.sleep(3000);


                progressDialog.dismiss();

                closeDialog();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            return null;
        }
    }
}
