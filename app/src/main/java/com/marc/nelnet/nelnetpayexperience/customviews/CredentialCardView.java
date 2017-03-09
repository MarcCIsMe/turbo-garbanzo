package com.marc.nelnet.nelnetpayexperience.customviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marc.nelnet.nelnetpayexperience.R;

/**
 * Created by Marc on 3/8/2017.
 */

public class CredentialCardView extends RelativeLayout {

    private View mView;
    private ImageView mLogo;
    private ImageView mStudentPicture;
    private ImageView mStudentPictureBackground;
    private DetailsLabelView mStudentDetails;
    private DetailsLabelView mBalanceDetails;
    private CardTouchListener mCardTouchListener;
    private CredentialCardViewListener mListener;
    private ImageView mSlideArrowUp;
    private TextView mSlideUpText;

    public interface CredentialCardViewListener {
        void onCardInserted();
    }

    public CredentialCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public CredentialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        setClipToPadding(false);
        mView = LayoutInflater.from(context).inflate(R.layout.credential_card, this, true).findViewById(R.id.card_view);
        mLogo = (ImageView)mView.findViewById(R.id.custom_client_logo);
        mStudentPicture = (ImageView)mView.findViewById(R.id.person_pic);
        mStudentPictureBackground = (ImageView)mView.findViewById(R.id.person_pic_background);
        mStudentDetails = (DetailsLabelView)mView.findViewById(R.id.student_details);
        mBalanceDetails = (DetailsLabelView)mView.findViewById(R.id.balance_details);
        mSlideArrowUp = (ImageView)mView.findViewById(R.id.slide_up_arrow1);
        mSlideUpText = (TextView)mView.findViewById(R.id.slide_up_text);
        Animation pulseRing = AnimationUtils.loadAnimation(context, R.anim.person_pic_pulse_ring);
        mStudentPictureBackground.setAnimation(pulseRing);
        Animation bounce = AnimationUtils.loadAnimation(context, R.anim.pay_card_arrow_bounce);
        mSlideArrowUp.setAnimation(bounce);
        mView.post(new Runnable() {
            @Override
            public void run() {
                mCardTouchListener = new CardTouchListener();
                mView.setOnTouchListener(mCardTouchListener);
            }
        });
    }

    public void setLogo(Drawable imageDrawable) {
        mLogo.setImageDrawable(imageDrawable);
    }

    public void setLogo(int imageResId) {
        mLogo.setImageResource(imageResId);
    }

    public void setStudentPicture(Drawable imageDrawable) {
        mStudentPicture.setImageDrawable(imageDrawable);
    }

    public void setStudentPicture(int imageResId) {
        mStudentPicture.setImageResource(imageResId);
    }
    
    public void setStudentName(String name) {
        mStudentDetails.setPrimaryText(name);
    }

    public void setBalance(String balance) {
        mBalanceDetails.setPrimaryText(balance);
    }

    public void setListener(CredentialCardViewListener listener) {
        mListener = listener;
    }

    public void resetCard() {
        mCardTouchListener.reset();
    }

    private void cardIsIn() {
        if(mListener != null) {
            mListener.onCardInserted();
        }
    }

    private class CardTouchListener implements OnTouchListener {
        private float initialY;
        private float finalYPos;
        private float deltaY;
        private boolean cardIsIn = false;

        public CardTouchListener() {
            int[] screenLocation = new int[2];
            mView.getLocationOnScreen(screenLocation);
            initialY = mView.getY(); //screenLocation[1];
            finalYPos = mView.getRootView().getY() - mView.getHeight() * 0.10f;
        }

        public void reset() {
            cardIsIn = false;
            animateCardToInitalPosition();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(cardIsIn) {
                return true;
            }
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    deltaY = mView.getY() - event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float newPosition = event.getRawY() + deltaY;
                    if(newPosition > initialY) {
                        newPosition = initialY;
                    } else if(newPosition < finalYPos) {
                        newPosition = finalYPos;
                        cardIsIn = true;
                        cardIsIn();
                    }
                    float alpha = (mView.getY() / (initialY - finalYPos));
                    mSlideArrowUp.setAlpha(alpha);
                    mSlideUpText.setAlpha(alpha);
                    mView.animate()
                            .y(newPosition)
                            .setDuration(0)
                            .start();
                    break;

                case MotionEvent.ACTION_UP:
                    animateCardToInitalPosition();
                    break;
                default:
                    return false;
            }

            return true;
        }
        private void setAlphaAnimation(View view) {
            view.animate()
                    .alpha(1)
                    .setDuration(300)
                    .start();
        }
        private void animateCardToInitalPosition() {
            mView.animate()
                    .y(initialY)
                    .setDuration(300)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
            setAlphaAnimation(mSlideArrowUp);
            setAlphaAnimation(mSlideUpText);
        }
    }

}
