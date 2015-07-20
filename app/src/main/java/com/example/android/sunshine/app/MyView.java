package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by Sheraz on 6/28/2015.
 */
public class MyView extends View {

    private float direction = 0;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean firstDraw;

    public MyView(Context context) {
        super(context);
        init(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(20);

        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setColor(getResources().getColor(R.color.primary_light));
        mTextPaint.setTextSize(18);

        firstDraw = true;
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        accessibilityEvent.getText().add(String.valueOf(direction));
        return true;
    }

    public void setPointerRotateAngle(float angle) {
        firstDraw = false;
        direction = angle;
        invalidate();

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int cxCompass = getMeasuredWidth()/2;
        int cyCompass = getMeasuredHeight()/2;
        float radiusCompass;

        if(cxCompass > cyCompass){
            radiusCompass = (float) (cyCompass * 0.9) - 12;
        }
        else{
            radiusCompass = (float) (cxCompass * 0.9) - 12;
        }

        if(!firstDraw){
            canvas.drawCircle(cxCompass - 2 / 2, cxCompass - 2 / 2, radiusCompass - 2, mPaint);
            canvas.drawRect(0, 0, getMeasuredWidth() - 2, getMeasuredWidth() - 2, mPaint);

            canvas.drawLine(cxCompass - 2 / 2, cxCompass - 2 / 2,
                    (float) (cxCompass - 2 / 2 + radiusCompass * Math.sin((double) (-direction) * 3.14 / 180)),
                    (float) (cxCompass - 2 / 2 - radiusCompass * Math.cos((double) (-direction) * 3.14 / 180)),
                    mPaint);

            canvas.drawText("W", cxCompass * 2 - 20, cxCompass, mTextPaint);
            canvas.drawText("E", 2, cxCompass, mTextPaint);

            canvas.drawText("N", cxCompass - 8, 15, mTextPaint);
            canvas.drawText("S", cxCompass - 9, cxCompass * 2 - 5, mTextPaint);

        }

    }
}
