package com.example.backgroundimage.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class PhenomenasTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public PhenomenasTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PhenomenasTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public PhenomenasTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        init(defStyle);
    }

    private void init() {

    }

    private void init(int style) {

    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }

}
