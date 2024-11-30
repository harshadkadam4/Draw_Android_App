package com.example.draw;

import android.content.Context;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private DrawingView drawingView;
    private ImageView eraser, paint_bucket, delete, save, open,bg_image;
    private SeekBar seekBar;
    private FrameLayout fLayout;

    float dX,dY;
    int lastAction;
    private EditText textBox;

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
        bg_image = findViewById(R.id.bg_image);

        textBox = findViewById(R.id.textBox);

        textBox.setOnTouchListener(this);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clear(fLayout);
                bg_image.setImageDrawable(null);
                textBox.setText("");
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
                drawingView.saveDrawingWithBackground(fLayout);
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
                    if (drawable != null) {
                        bg_image.setImageDrawable(drawable);

                        // Get layout width and calculate proportional height
                        int layoutWidth = fLayout.getWidth();
                        int intrinsicWidth = drawable.getIntrinsicWidth();
                        int intrinsicHeight = drawable.getIntrinsicHeight();

                        if (intrinsicWidth > 0) {
                            int scaledHeight = (intrinsicHeight * layoutWidth) / intrinsicWidth;
                            bg_image.getLayoutParams().height = scaledHeight;
                            bg_image.getLayoutParams().width = layoutWidth;
                            bg_image.requestLayout();
                        }

                        // Set scaleType to maintain aspect ratio
                        bg_image.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
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

 /*   @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    // Trigger text input only on tap, not on drag
                    view.performClick();
                }
                break;

            default:
                return false;
        }

        // Allow EditText to process touch events (e.g., cursor, keyboard)
        return lastAction == MotionEvent.ACTION_MOVE;
    } */

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    // Trigger text input and show the keyboard immediately
                    view.performClick();
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        editText.requestFocus();

                        // Show the keyboard
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }
                break;

            default:
                return false;
        }

        // Allow the EditText to handle its own touch events for text input
        return lastAction != MotionEvent.ACTION_MOVE;
    }


}















