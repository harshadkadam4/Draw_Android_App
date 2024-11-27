package com.example.draw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private ImageView eraser, paint_bucket, delete, save, open;
    private SeekBar seekBar;
    private FrameLayout fLayout;

    private int REQUEST_PICK_IMAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawingView = findViewById(R.id.drawingView);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
        eraser = findViewById(R.id.eraser);
        paint_bucket = findViewById(R.id.paint_bucket);
        seekBar = findViewById(R.id.seekBar);
        open = findViewById(R.id.open);
        fLayout = findViewById(R.id.fLayout);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clear(fLayout);
            }
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.erase();
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // drawingView.saveFrameLayout(fLayout);
                drawingView.saveDrawingOnly(fLayout);
            }
        });

    }

    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) { // Added null check
            if (requestCode == REQUEST_PICK_IMAGE) {
                Uri uri = data.getData();
                if (uri != null) { // Check if URI is not null
                    Bitmap bitmap = loadFromUri(uri);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    fLayout.setBackground(drawable);
                    //drawingView.setLayoutBG(drawable); // Assuming setLayoutBG() is defined in DrawingView
                }
            }
        }
    }


    private Bitmap loadFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source, (decoder, info, src) ->
                        decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE) // Force software rendering
                );
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

















