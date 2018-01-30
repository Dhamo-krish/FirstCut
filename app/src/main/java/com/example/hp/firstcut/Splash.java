package com.example.hp.firstcut;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.StreamSpecification;
import com.amazonaws.services.dynamodbv2.model.StreamViewType;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Splash extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView tv;
    Handler handler=new Handler();
    boolean visible;
    GoogleApiClient googleApiClient;
    private static int ReqCode = 100;
    SharedPreferences sharedPreferences ;
    AmazonDynamoDBClient ddbclient;
    String name,email,url;


    AWSCredentials credentials = new BasicAWSCredentials("AKIAID5UFVAFVQCYVHRA","xdHWdVe7ROxGZXrbcAaegMmXwFAx0buAwC7zDdgq");




    TransitionManager transitionManager=new TransitionManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.title);
        final ViewGroup transitionsContainer = (ViewGroup) findViewById(R.id.transitions);
        final Button signin = (Button) transitionsContainer.findViewById(R.id.signin);
        Typeface face=Typeface.createFromAsset(getAssets(),"GlacialIndifference-Regular.ttf");
        tv.setTypeface(face);
        sharedPreferences = getSharedPreferences("Users",MODE_PRIVATE);


        Boolean islog = sharedPreferences.getBoolean("isLogged",false);
        if(islog){

            startActivity(new Intent(Splash.this,ProjectActivity.class));
            finish();
        }



        ddbclient = new AmazonDynamoDBClient(credentials);
        ddbclient.setRegion(Region.getRegion(Regions.US_EAST_1));

        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
        signin.setTypeface(face);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                transitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                signin.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }, 2000);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(Splash.this,ProjectActivity.class));
                signin();
            }
        });
    }
    void signin(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, ReqCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ReqCode){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            name = account.getDisplayName().replace(" ", "");
            email = account.getEmail();
            if(account.getPhotoUrl().toString().isEmpty()){
                url= "";
            }else{
                url = account.getPhotoUrl().toString();
            }


            System.out.println("Bow "+ name + email + url);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putBoolean("isLogged",true);
            editor.putString("name",name);
            editor.putString("email",email);
            editor.commit();

            new Create_Table_Task().execute();


            startActivity(new Intent(Splash.this,ProjectActivity.class));
            finish();


        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public class Create_Table_Task extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {

            List<AttributeDefinition> attributeDefinitions= new ArrayList<>();
            attributeDefinitions.add(new AttributeDefinition().withAttributeName("Email").withAttributeType(ScalarAttributeType.S));

            List<KeySchemaElement> keySchemaElements=new ArrayList<>();
            keySchemaElements.add(new KeySchemaElement().withAttributeName("Email").withKeyType(KeyType.HASH));


            CreateTableRequest request=new CreateTableRequest().withTableName(name)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withKeySchema(keySchemaElements)
                    .withStreamSpecification(new StreamSpecification().withStreamEnabled(Boolean.TRUE).withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

            try{
                CreateTableResult result = ddbclient.createTable(request);
                System.out.println("Table Creation Results "+ result.getTableDescription());
                new AddItem_Task().execute();

            }catch (Exception e){
                System.out.println("bow");
                new AddItem_Task().execute();
            }
            return null;
        }
    }
    public class AddItem_Task extends  AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            Map<String , AttributeValue> data=new HashMap<>();



            data.put("Email",new AttributeValue().withS(email));
            data.put("Username",new AttributeValue().withS(name));
            data.put("Url",new AttributeValue().withS(url));




            System.out.println("Map Values are : "+ data);
            List<String> tablees=ddbclient.listTables().getTableNames();

            System.out.println("Tables : "+tablees);


            PutItemRequest putItemRequest=new PutItemRequest().withTableName(name).withItem(data);
//            ddb.putItem(putItemRequest);
            try{
                PutItemResult result=ddbclient.putItem(putItemRequest);
                System.out.println("Item Success : "+result.getAttributes());
            }catch (Exception e){

                new AddItem_Task().execute();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(dynamo_db.this, "Press the Button again...", Toast.LENGTH_SHORT).show();
//                        }
//                    });
            }







            return null;
        }
    }

}
