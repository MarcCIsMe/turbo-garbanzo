package com.marc.nelnet.nelnetpayexperience.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.marc.nelnet.nelnetpayexperience.R;

/**
 * Created by Marc on 3/9/2017.
 */

public class HorizontalDetailLabelView extends android.support.percent.PercentRelativeLayout {

    private View mView;
    private TextView mPrimaryTextView;
    private TextView mSecondaryTextView;

    public HorizontalDetailLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public HorizontalDetailLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        mView = LayoutInflater.from(context).inflate(R.layout.horizontal_detail_label_view, this, true);
        mPrimaryTextView = (TextView)mView.findViewById(R.id.primary_text);
        mSecondaryTextView = (TextView)mView.findViewById(R.id.secondary_text);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalDetailLabelView, 0, 0);
        if(a.hasValue(R.styleable.HorizontalDetailLabelView_hDetailsPrimaryText)) {
            mPrimaryTextView.setText(a.getText(R.styleable.HorizontalDetailLabelView_hDetailsPrimaryText));
        }
        if(a.hasValue(R.styleable.HorizontalDetailLabelView_hDetailsSecondaryText)) {
            mSecondaryTextView.setText(a.getText(R.styleable.HorizontalDetailLabelView_hDetailsSecondaryText));
        }
    }

    public void setPrimaryText(String text) {
        mPrimaryTextView.setText(text);
    }

    public void setSecondaryText(String text) {
        mSecondaryTextView.setText(text);
    }
}
