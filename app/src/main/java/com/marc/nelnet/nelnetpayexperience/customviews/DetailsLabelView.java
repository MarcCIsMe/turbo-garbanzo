package com.marc.nelnet.nelnetpayexperience.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marc.nelnet.nelnetpayexperience.R;

/**
 * Created by Marc on 3/8/2017.
 */

public class DetailsLabelView extends LinearLayout {

    private View mView;
    private TextView mPrimaryTextView;
    private TextView mSecondaryTextView;

    public DetailsLabelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public DetailsLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        mView = LayoutInflater.from(context).inflate(R.layout.details_label_view, this, true);
        mPrimaryTextView = (TextView)mView.findViewById(R.id.primary_text);
        mSecondaryTextView = (TextView)mView.findViewById(R.id.secondary_text);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DetailsLabelView, 0, 0);
        if(a.hasValue(R.styleable.DetailsLabelView_detailsPrimaryText)) {
            mPrimaryTextView.setText(a.getText(R.styleable.DetailsLabelView_detailsPrimaryText));
        }
        if(a.hasValue(R.styleable.DetailsLabelView_detailsSecondaryText)) {
            mSecondaryTextView.setText(a.getText(R.styleable.DetailsLabelView_detailsSecondaryText));
        }
    }

    public void setPrimaryText(String text) {
        mPrimaryTextView.setText(text);
    }

    public void setSecondaryText(String text) {
        mSecondaryTextView.setText(text);
    }
}
