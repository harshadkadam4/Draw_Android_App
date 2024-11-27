package com.example.draw;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Environment;
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

    public void setLayoutBG(Drawable drawable) {
        if (fLayout != null) {
            fLayout.setBackground(drawable); // Set Drawable as background
        }
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
/*
    public void saveFrameLayout() {
        // Enable drawing cache
        setDrawingCacheEnabled(true);
        buildDrawingCache();

        // Capture the bitmap
        Bitmap originalBitmap = getDrawingCache();
        if (originalBitmap != null) {
            try {
                // Create a new bitmap to include the current background
                Bitmap bitmapWithBackground = Bitmap.createBitmap(
                        originalBitmap.getWidth(),
                        originalBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888
                );

                // Get the FrameLayout's background drawable
                Drawable backgroundDrawable = getBackground();

                // Draw the current background and the original bitmap onto the new bitmap
                Canvas canvas = new Canvas(bitmapWithBackground);
                if (backgroundDrawable != null) {
                    backgroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    backgroundDrawable.draw(canvas); // Draw the current background
                }
                canvas.drawBitmap(originalBitmap, 0, 0, null); // Draw the content

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
*/

 /*   // Method to save FrameLayout content as an image
    public void saveFrameLayout(FrameLayout frameLayout) {
        try {
            // Create a Bitmap with the same dimensions as the FrameLayout
            Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Get the background drawable of the FrameLayout
            Drawable background = frameLayout.getBackground();
            if (background != null) {
                // Draw the background onto the canvas
                background.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                background.draw(canvas);
            } else {
                // If no background is set, fill with a default color (e.g., white)
                canvas.drawColor(Color.WHITE);
            }

            // Draw the FrameLayout's contents onto the canvas
            frameLayout.draw(canvas);

            // Save the bitmap to a file
            String fileName = "drawing.png";
            File filePath = new File(getContext().getExternalFilesDir(null), fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            // Notify user of success
            Toast.makeText(getContext(), "Saved at: " + filePath.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
*/

    public void saveDrawingOnly(FrameLayout frameLayout) {
        try {
            // Capture only the DrawingView content as a Bitmap
            DrawingView drawingView = frameLayout.findViewById(R.id.drawingView);
            Bitmap bitmap = Bitmap.createBitmap(drawingView.getWidth(), drawingView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Draw the FrameLayout's background if necessary
            Drawable backgroundDrawable = frameLayout.getBackground();
            if (backgroundDrawable != null) {
                backgroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                backgroundDrawable.draw(canvas);
            }

            // Draw the DrawingView content
            drawingView.draw(canvas);

            // Save the bitmap to a file
            String fileName = "drawing_without_ui.png";
            File filePath = new File(getContext().getExternalFilesDir(null), fileName);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            // Notify the user
            Toast.makeText(getContext(), "Saved at: " + filePath.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }



    // Capture FrameLayout content as a Bitmap
    private Bitmap captureFrameLayout(FrameLayout frameLayout) {
        Bitmap bitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        frameLayout.draw(canvas);
        return bitmap;
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


