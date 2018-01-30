package com.example.hp.firstcut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.hp.firstcut.Adapters.DummyAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageViewActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    Button close;
    ImageView image;
    AmazonS3 s3;
    URL AWSImgUrl;
    AWSCredentials credentials = new BasicAWSCredentials("AKIAID5UFVAFVQCYVHRA","xdHWdVe7ROxGZXrbcAaegMmXwFAx0buAwC7zDdgq");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);

//        String url=getIntent().getExtras().get("key").toString();
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.picture);
        image=(ImageView)findViewById(R.id.picture);
        close=(Button)findViewById(R.id.dummy_button);

        s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        new GetImageURl_Task().execute();
//        if(url.equals(""))
//        {
//            System.out.println("NOT DONE"+url);
//        }
//        else
//        {
//            System.out.println("DONE"+url);
//        }


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    public class GetImageURl_Task extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {

            SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);

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
                    if(DummyAdapter.s3path.equals(split[1])){
                        if(DummyAdapter.Activity_Name.equals(split[2])){
                            Date expiration = new Date();
                             long sec = expiration.getTime();
                             sec += 1000*60*60;
                             expiration.setTime(sec);


                            File file =new File(Environment.getExternalStorageDirectory()+"/FirstCut/"+DummyAdapter.s3path+"/"+DummyAdapter.Activity_Name);
                            if(file.mkdir()){
                                File newFile = new File(file.getPath());

                            }


                            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest("com.jusdraw/"+name+"/"+DummyAdapter.s3path,DummyAdapter.Activity_Name);
                            urlRequest.setMethod(HttpMethod.GET);
                            urlRequest.setExpiration(expiration);
                            AWSImgUrl = s3.generatePresignedUrl(urlRequest);
                            try {
                                final Bitmap bitmap = BitmapFactory.decodeStream(AWSImgUrl.openConnection().getInputStream());
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       image.setImageBitmap(bitmap);
                                   }
                               });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }
//                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("firstcutapplication/"+)
            }
            return null;
        }
    }
}
