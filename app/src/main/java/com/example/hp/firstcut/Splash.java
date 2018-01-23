package com.example.hp.firstcut;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Splash extends AppCompatActivity {
        TextView tv;
    Handler handler=new Handler();
    boolean visible;
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
                startActivity(new Intent(Splash.this,ProjectActivity.class));
            }
        });
    }
}
