package com.example.hp.firstcut;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.example.hp.firstcut.Adapters.ProItemAdapter;
import com.example.hp.firstcut.Adapters.Project;
import com.example.hp.firstcut.Fragments.ProjectFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {

    ProjectFragment projectFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);


        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.sign_out){
            Toast.makeText(this, "bowww....", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences("Users",MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();

            editor.putBoolean("LoggedOut",true);
            editor.commit();
            startActivity(new Intent(ProjectActivity.this, Splash.class));
        }
        return true;
    }

    public static FloatingActionButton fab;
    String Project_Name;
    ProgressDialog progressDialog;
    Dialog dialog1;
    PutObjectRequest putObjectRequest;
    AmazonS3 s3;
    AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3QRFSJLAJP5U3GA","JnttF8Wooim3B5n+SrKnzeH/47GEUykKf+bYRmkz");


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





        s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_WEST_2));

         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BackFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                //showChangeLangDialog();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.frame_container,new ProjectFragment()).commit();
    }


    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.activityname);


        dialogBuilder.setTitle("Creating Project");
        dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Project_Name = edt.getText().toString().replace(" ","");
                //do something with edt.getText().toString();
                dialog1 =(Dialog)dialog;

                new S3_Upload_Task().execute();
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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

    public class S3_Upload_Task extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getApplicationContext());
//            progressDialog.setMessage("Creating Project.....");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
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
            putObjectRequest = new PutObjectRequest("com.jusdraw/"+Project_Name,Project_Name+".txt",newfile);

            PutObjectResult result = s3.putObject(putObjectRequest);
            System.out.println("Bowwwwww"+result);

            try {
                Thread.sleep(3000);


                //progressDialog.dismiss();

                getFragmentManager().beginTransaction().replace(R.id.frame_container,new ProjectFragment()).commit();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            return null;
        }
    }



}
