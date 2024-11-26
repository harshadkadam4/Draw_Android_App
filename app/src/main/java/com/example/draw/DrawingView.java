package com.example.draw;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import yuku.ambilwarna.AmbilWarnaDialog;
import android.graphics.Bitmap;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.FileOutputStream;

public class DrawingView extends View {
    private Paint paint;
    private Path currentPath;
    private List<Path> paths = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private List<Float> strokeWidths = new ArrayList<>();
   // private boolean isEraser = false;
    private int currentColor = Color.RED;
    private float stroke = 10f;

    private FrameLayout fLayout;


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpPaint();
    }

    public void setFrameLayout(FrameLayout frameLayout) {
        this.fLayout = frameLayout;
    }

    public void setLayoutBG(Drawable drawable)
    {
        fLayout.setBackground(drawable);
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

    public void openColorPickerDialogue() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(getContext(), currentColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        currentColor = color;
                    }
                });
        colorPickerDialogue.show();
    }

    public void seekChange(int progress)
    {
        stroke = progress;
        invalidate();
    }

    public void saveFrameLayout() {
        // Enable drawing cache
        setDrawingCacheEnabled(true);
        buildDrawingCache();

        // Capture the bitmap
        Bitmap originalBitmap = getDrawingCache();
        if (originalBitmap != null) {
            try {
                // Create a new bitmap with a white background
                Bitmap bitmapWithBackground = Bitmap.createBitmap(
                        originalBitmap.getWidth(),
                        originalBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888
                );

                // Draw the white background and the original bitmap onto the new bitmap
                Canvas canvas = new Canvas(bitmapWithBackground);
                canvas.drawColor(Color.WHITE); // Draw white background
                canvas.drawBitmap(originalBitmap, 0, 0, null);

                // Define file path (app-specific external storage)
                File filePath = new File(getContext().getExternalFilesDir(null), "drawing.png");
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                // Save the new bitmap to the file
                bitmapWithBackground.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                // Show confirmation message
                Toast.makeText(getContext(), "Drawing saved to: " + filePath.getAbsolutePath(), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error saving drawing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                // Disable drawing cache
                setDrawingCacheEnabled(false);
                destroyDrawingCache();
            }
        } else {
            Toast.makeText(getContext(), "Error capturing drawing.", Toast.LENGTH_SHORT).show();
        }
    }



//    public void toggleEraser()
//    {
//        isEraser = !isEraser;
//        paint.setColor(isEraser ? Color.WHITE : Color.BLACK);
//        paint.setStrokeWidth(isEraser ? 20 : 10);
//    }

    // Method to clear the drawing
    public void clear() {
       paths.clear();
       colors.clear();
       strokeWidths.clear();
       //fLayout.setBackground(Color.WHITE);
       invalidate();
    }

    public void erase()
    {
        currentColor = Color.WHITE;
        invalidate();
    }


}


