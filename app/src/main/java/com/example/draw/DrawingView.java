package com.example.draw;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import yuku.ambilwarna.AmbilWarnaDialog;

public class DrawingView extends View {
    private Paint paint;
    private Path currentPath;
    private List<Path> paths = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private List<Float> strokeWidths = new ArrayList<>();
   // private boolean isEraser = false;
    private int currentColor = Color.BLACK;
    private float stroke = 10f;


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpPaint();
    }

    private void setUpPaint()
    {
        // Set up paint properties
        paint = new Paint();
        paint.setColor(currentColor);          // Color of the paint
        paint.setStyle(Paint.Style.STROKE);    // Paint style (STROKE, FILL, or FILL_AND_STROKE)
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
       invalidate();
    }
}


