package com.example.draw;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import yuku.ambilwarna.AmbilWarnaDialog;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;

public class DrawingView extends View {
    private Paint paint;
    private Path currentPath;
    private List<Path> paths = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private List<Float> strokeWidths = new ArrayList<>();
    //private boolean isEraser = false;
    private int currentColor = Color.RED;
    private int tempColor;
    private float stroke = 10f;

    //private ImageView eraser = findViewById(R.id.eraser);
    private FrameLayout fLayout;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpPaint();
    }

    private void setUpPaint()
    {
        // Set up paint properties
        paint = new Paint();
        paint.setColor(currentColor);          // Color of the paint
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);// Paint style (STROKE, FILL, or FILL_AND_STROKE)
        paint.setStrokeWidth(stroke);              // Stroke width
        paint.setAntiAlias(true);              // Smooth edges
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPath(path, paint); // Draw the path on the canvas

        for(int i=0; i < paths.size(); i++)
        {
            paint.setColor(colors.get(i));
            paint.setStrokeWidth(strokeWidths.get(i));
            canvas.drawPath(paths.get(i), paint);
        }

        if(currentPath != null)
        {
            paint.setColor(currentColor);
            paint.setStrokeWidth(stroke);
            canvas.drawPath(currentPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);   // Start a new line
                return true;

            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);   // Draw line to this point
                break;

            case MotionEvent.ACTION_UP:
                paths.add(currentPath);
                colors.add(currentColor);
                strokeWidths.add(stroke);
                currentPath = null;
                break;
        }
        invalidate(); // Re-draw the view
        return true;
    }

    public void openColorPickerDialogue(ImageView eraser) {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(getContext(), currentColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        currentColor = color;
                        if(eraser != null)
                        {
                            eraser.setColorFilter(Color.BLACK);
                        }
                        tempColor = currentColor;
                    }
                });
        colorPickerDialogue.show();

    }

    public void seekChange(int progress)
    {
        stroke = progress;
        invalidate();
    }

    public void saveDrawingWithBackground(FrameLayout frameLayout) {
        try {
            // Create a Bitmap with the same dimensions as the FrameLayout
            Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Draw the FrameLayout's background (if any)
            Drawable backgroundDrawable = frameLayout.getBackground();
            if (backgroundDrawable != null) {
                backgroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                backgroundDrawable.draw(canvas);
            }

            // Draw the ImageView's content at its actual position
            ImageView backgroundImage = frameLayout.findViewById(R.id.bg_image);
            if (backgroundImage != null && backgroundImage.getDrawable() != null) {
                // Save the ImageView's transformation and position
                int saveId = canvas.save();

                // Apply ImageView transformations
                canvas.translate(backgroundImage.getLeft(), backgroundImage.getTop());
                backgroundImage.getDrawable().draw(canvas);

                // Restore the canvas to avoid affecting subsequent drawings
                canvas.restoreToCount(saveId);
            }

            // Add TextBoxes
            for(int i =frameLayout.getChildCount() - 1; i>=0 ; i--)
            {
                View view = frameLayout.getChildAt(i);
                if(view instanceof EditText)
                {
                    if(view != null)
                    {
                        int saveId = canvas.save();
                        canvas.translate(view.getX(), view.getY());
                        view.draw(canvas);
                        canvas.restoreToCount(saveId);
                    }
                }
            }

            // Draw the DrawingView content
            DrawingView drawingView = frameLayout.findViewById(R.id.drawingView);
            if (drawingView != null)
            {
                drawingView.draw(canvas);
            }

            String fileName = "drawing.png";
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                try (OutputStream os = frameLayout.getContext()
                        .getContentResolver()
                        .openOutputStream(frameLayout.getContext()
                                .getContentResolver()
                                .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            // Notify the user
            Toast.makeText(frameLayout.getContext(), "Saved in Downloads", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(frameLayout.getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    // Capture FrameLayout content as a Bitmap
    private Bitmap captureFrameLayout(FrameLayout frameLayout) {
        Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        frameLayout.draw(canvas);
        return bitmap;
    }

    // Method to clear the drawing
    public void clear(FrameLayout frameLayout) {
       paths.clear();
       colors.clear();
       strokeWidths.clear();
       frameLayout.setBackgroundResource(R.color.white);
       currentColor= tempColor = Color.RED;
       paint.setColor(currentColor);
       invalidate();
    }

    public void erase(int flg)
    {
        if(flg==0)
        {
            tempColor = currentColor;
            currentColor = Color.WHITE;
            invalidate();
        } else
        {
            currentColor = tempColor;
            invalidate();
        }
    }
}


