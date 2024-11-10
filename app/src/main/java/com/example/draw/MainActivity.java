package com.example.draw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private ImageView eraser,paint_bucket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingView);
        eraser = findViewById(R.id.eraser);
        paint_bucket = findViewById(R.id.paint_bucket);

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clear();
            }
        });

        paint_bucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.openColorPickerDialogue();
            }
        });


//        drawingView = new DrawingView(this);
//        setContentView(drawingView);
    }
}