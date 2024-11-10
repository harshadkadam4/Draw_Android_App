package com.example.draw;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import yuku.ambilwarna.AmbilWarnaDialog;


public class DrawingView extends View {
    private Paint paint;
    private Path path;
    private boolean isEraser = false;
    private int mDefaultColor;


//    private View mColorPreview;
//    private int pick_color;

    public DrawingView(Context context) {
        super(context);
        path = new Path();
        paint = new Paint();
        setUpPaint();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        setUpPaint();
    }


    private void setUpPaint()
    {
        // Set up paint properties
        paint.setColor(Color.BLACK);          // Color of the paint
        paint.setStyle(Paint.Style.STROKE);    // Paint style (STROKE, FILL, or FILL_AND_STROKE)
        paint.setStrokeWidth(10);              // Stroke width
        paint.setAntiAlias(true);              // Smooth edges
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint); // Draw the path on the canvas
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);   // Start a new line
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);   // Draw line to this point
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate(); // Re-draw the view
        return true;
    }

    public void openColorPickerDialogue() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(getContext(), mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mDefaultColor = color;
                        paint.setColor(color);
                        invalidate();
                    }
                });
        colorPickerDialogue.show();
    }


    public void toggleEraser()
    {
        isEraser = !isEraser;
        paint.setColor(isEraser ? Color.WHITE : Color.BLACK);
        paint.setStrokeWidth(isEraser ? 20 : 10);
    }

    // Method to clear the drawing
    public void clear() {
        path.reset();
        invalidate();
    }
}
