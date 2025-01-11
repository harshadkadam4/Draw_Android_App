package com.example.draw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.IOException;
public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private DrawingView drawingView;
    private ImageView eraser, paint_bucket, delete, save, open,bg_image;
    private SeekBar seekBar;
    private FrameLayout fLayout;

    float dX,dY;
    int lastAction,flg=0;
    private EditText textBox;

    private int REQUEST_PICK_IMAGE = 1000;

    //Menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_textbox:
                try {
                    FrameLayout rootLayout = findViewById(R.id.fLayout);
                    EditText newTextBox = new EditText(this);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );

                    params.leftMargin = 50;
                    params.topMargin = 50;

                    newTextBox.setLayoutParams(params);
                    newTextBox.setId(View.generateViewId());
                    newTextBox.setHint("Add text");
                    newTextBox.setBackgroundColor(Color.TRANSPARENT);
                    newTextBox.setTextSize(18);
                    newTextBox.setTextColor(Color.BLACK);
                    newTextBox.setPadding(30,8,0,4);

                    Typeface typeface = ResourcesCompat.getFont(this,R.font.poppins_regular);
                    newTextBox.setTypeface(typeface);
                    rootLayout.addView(newTextBox);

                    newTextBox.setOnTouchListener(this);

                    Toast.makeText(this, "TextBox Added", Toast.LENGTH_SHORT).show();

                }catch (Exception e)
                {
                    Toast.makeText(this, "Err "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                 return true;

            case R.id.ok2:
                Toast.makeText(this,"OK2",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.ok3:
                Toast.makeText(this,"OK3",Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

       // getSupportActionBar().hide();

        drawingView = findViewById(R.id.drawingView);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
        eraser = findViewById(R.id.eraser);
        paint_bucket = findViewById(R.id.paint_bucket);
        seekBar = findViewById(R.id.seekBar);
        open = findViewById(R.id.open);
        fLayout = findViewById(R.id.fLayout);
        bg_image = findViewById(R.id.bg_image);


        // Setting OnTouchListener to TextBox
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
                //eraser.setBackgroundResource(R.drawable.imageview_bg);

                if(flg==0)
                {
                    drawingView.erase(0);
                    //eraser.setBackgroundResource(R.drawable.imageview_bg);
                    eraser.setColorFilter(getColor(R.color.purple_200));
                    flg=1;
                }
                else {
                    drawingView.erase(1);
                    eraser.setColorFilter(getColor(R.color.black));
                    flg=0;
                }
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

    //Image Adding
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

    // Movable Textbox
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
                }
                break;

            default:
                return false;
        }

        // Allow the EditText to handle its own touch events for text input
        return lastAction == MotionEvent.ACTION_MOVE;
    }


}















