package com.marc.nelnet.nelnetpayexperience.customviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.marc.nelnet.nelnetpayexperience.R;

/**
 * Created by Marc on 3/8/2017.
 */

public class RingRippleController extends RelativeLayout {

    private Context mContext;
    private View mView;
    private ImageView ring1;
    private ImageView ring2;
    private ImageView ring3;

    public RingRippleController(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public RingRippleController(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.ring_ripple, this, true);
        ring1 = (ImageView)mView.findViewById(R.id.pay_ring1);
        ring2 = (ImageView)mView.findViewById(R.id.pay_ring2);
        ring3 = (ImageView)mView.findViewById(R.id.pay_ring3);
    }

    public void startAnimation() {
        ring1.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Animation scaleAndFade = AnimationUtils.loadAnimation(mContext, R.anim.ring_scale_fade);
                ring1.startAnimation(scaleAndFade);
            }
        }, getResources().getInteger(R.integer.ring1DelayedStart));
        ring2.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Animation scaleAndFade = AnimationUtils.loadAnimation(mContext, R.anim.ring_scale_fade);
                ring2.startAnimation(scaleAndFade);
            }
        }, getResources().getInteger(R.integer.ring2DelayedStart));

        ring3.postOnAnimationDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Animation scaleAndFade = AnimationUtils.loadAnimation(mContext, R.anim.ring_scale_fade);
                ring3.startAnimation(scaleAndFade);
            }
        }, getResources().getInteger(R.integer.ring3DelayedStart));
    }

    public void stopAnimation() {
        ring1.setAnimation(null);
        ring2.setAnimation(null);
        ring3.setAnimation(null);
    }
}
