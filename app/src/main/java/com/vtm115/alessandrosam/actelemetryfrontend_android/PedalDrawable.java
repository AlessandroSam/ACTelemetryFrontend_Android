package com.vtm115.alessandrosam.actelemetryfrontend_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

/**
 * Created by DRiVER on 21.11.2015.
 */

public class PedalDrawable extends View {
    private ShapeDrawable mDrawable;
    private int x = 0;
    private int y = 0;
   private int width = 0;
    private int height = 0;
    private int gaugeValue = 0;

    public PedalDrawable(Context context) {
        super(context);
        mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(0xff74AC23);  // Color?
        mDrawable.setBounds(x, y, x + width, y + height);
    }

    public void setGaugeValue(int val) {
        gaugeValue = val;
    }

    protected void onDraw(Canvas canvas) {
        mDrawable.setBounds(0, 0, getRight()*(gaugeValue/100), getBottom());
        mDrawable.draw(canvas);
    }
}
