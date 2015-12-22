package com.vtm115.alessandrosam.actelemetryfrontend_android;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Alessandro on 07.09.2015.
 */
public class LCDTextView extends TextView {
    private static final String TTF_NAME = "fonts/ericssonga-628.TTF"; // set the typeface from src/main/assets
    private static Typeface typeface;

    public LCDTextView(final Context context) {
        this(context, null);
    }

    public LCDTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LCDTextView(final Context context, final AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), TTF_NAME);
        }
        setTypeface(typeface);
    }
}
