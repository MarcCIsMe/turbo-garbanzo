package com.marc.nelnet.nelnetpayexperience.fragments;

import android.content.Context;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;

import com.marc.nelnet.nelnetpayexperience.R;
import com.marc.nelnet.nelnetpayexperience.customviews.CredentialCardView;
import com.marc.nelnet.nelnetpayexperience.customviews.RippleRings;

/**
 * Created by Marc on 3/8/2017.
 */

public class PayFragment extends BaseFragment implements CredentialCardView.CredentialCardViewListener {

    private CredentialCardView mCredentialCardView;
    private RippleRings mRippleRings;
    private Button mCancelButton;

    public static PayFragment newInstance() {
        PayFragment fragment = new PayFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setUpView();
    }

    @Override
    public String getScreenName() {
        return "Pay";
    }

    private void setUpView() {
        // Hide shadow outline for shape covering top of card slot.
        View cardSlotTopCover = getActivity().findViewById(R.id.card_slot_top_cover);
        cardSlotTopCover.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setAlpha(0);
            }
        });

        String studentName = "Nelnet B. Solutions";
        String balance = "$123.45";
        int logoPicResId = R.drawable.ridgeview_transparent;
        int studentPicResId = R.drawable.pay_pic;

        mCredentialCardView = (CredentialCardView)getActivity().findViewById(R.id.payment_card);
        mCredentialCardView.setListener(this);
        mCredentialCardView.setLogo(logoPicResId);
        mCredentialCardView.setStudentPicture(studentPicResId);
        mCredentialCardView.setStudentName(studentName);
        mCredentialCardView.setBalance(balance);

        mRippleRings = (RippleRings)getActivity().findViewById(R.id.rings);
        mCancelButton = (Button)getActivity().findViewById(R.id.cancel_pay);
        mCancelButton.setAlpha(0);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateAlpha(mCancelButton, 0, 300);
                mCredentialCardView.resetCard();
            }
        });
    }

    private void animateAlpha(View view, float alpha, long duration) {
        view.animate()
                .alpha(alpha)
                .setDuration(duration)
                .start();
    }

    @Override
    public void onCardInserted() {
        mRippleRings.stopAnimation();
        animateAlpha(mRippleRings, 1, 300);
        mRippleRings.startAnimation();
        animateAlpha(mCancelButton, 1, 300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateAlpha(mRippleRings, 0, 300);
            }
        }, 2500);
    }
}
