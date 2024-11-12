package com.example.draw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private ImageView eraser,paint_bucket;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingView);
        eraser = findViewById(R.id.eraser);
        paint_bucket = findViewById(R.id.paint_bucket);
        seekBar = findViewById(R.id.seekBar);

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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawingView.seekChange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        drawingView = new DrawingView(this);
//        setContentView(drawingView);
    }
}