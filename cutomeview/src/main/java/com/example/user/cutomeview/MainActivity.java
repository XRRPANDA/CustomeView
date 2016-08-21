package com.example.user.cutomeview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    Button circle;
    Button viewgroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circle = (Button) findViewById(R.id.circle);
        viewgroup = (Button) findViewById(R.id.viewgroup);

        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this,CircleActivity.class);
                startActivity(intent);
            }
        });

        viewgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ViewGroupActivity.class);
                startActivity(intent);
            }
        });
    }
}
