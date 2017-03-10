package com.marc.nelnet.nelnetpayexperience.fragments;

import android.content.Context;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.percent.PercentRelativeLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;

import com.marc.nelnet.nelnetpayexperience.AuthorizationSheetBehavior;
import com.marc.nelnet.nelnetpayexperience.R;
import com.marc.nelnet.nelnetpayexperience.customviews.CredentialCardView;
import com.marc.nelnet.nelnetpayexperience.customviews.RippleRings;

/**
 * Created by Marc on 3/8/2017.
 */

public class PayFragment extends BaseFragment implements CredentialCardView.CredentialCardViewListener {
    private CredentialCardView mCredentialCardView;
    private RippleRings mRings;
    private Button mCancelButton;
    private PercentRelativeLayout mAuthorizeView;
    private Handler mAwaitingAuthSheetHandler;
    private View mPayDetailsView;
    private View mSuccessView;
    private Button mAuthorizeButton;
    private Button mAuthCancelButton;
    private ImageView mAuthXButton;
    private boolean mIsSliding = false;

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
        return inflater.inflate(R.layout.fragment_pay, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView();
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

        initializeCredentialCard();

        mRings = (RippleRings)getActivity().findViewById(R.id.rings);
        mAuthorizeView = (PercentRelativeLayout)getActivity().findViewById(R.id.bottom_sheet);
        mPayDetailsView = mAuthorizeView.findViewById(R.id.auth_pay_details);
        mSuccessView = mAuthorizeView.findViewById(R.id.auth_success);

        final AuthorizationSheetBehavior bottomSheetBehavior = (AuthorizationSheetBehavior)BottomSheetBehavior.from(mAuthorizeView);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mAuthorizeButton = (Button)mAuthorizeView.findViewById(R.id.authorize_button);
        mAuthorizeButton.setClickable(true);
        mAuthorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                showSuccess(true);
            }
        });

        mCancelButton = (Button)getActivity().findViewById(R.id.cancel_pay);
        mCancelButton.setAlpha(0);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwaitingAuthSheetHandler.removeCallbacksAndMessages(null);
                animateAlpha(mCancelButton, 0, getResources().getInteger(R.integer.alphaFadeDuration));
                animateAlpha(mRings, 0, getResources().getInteger(R.integer.alphaFadeDuration));
                mCredentialCardView.resetCard();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        View.OnClickListener closeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAuthorizationSheet(false);
                mCredentialCardView.resetCard();
            }
        };

        mAuthXButton = (ImageView)mAuthorizeView.findViewById(R.id.close_authorization);
        mAuthXButton.setOnClickListener(closeClickListener);

        mAuthCancelButton = (Button)mAuthorizeView.findViewById(R.id.cancel_authorization);
        mAuthCancelButton.setOnClickListener(closeClickListener);
    }

    private void initializeCredentialCard() {
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
    }

    private void showAuthorizationSheet(boolean show) {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(mAuthorizeView);
        int state;
        if(show) {
            state = BottomSheetBehavior.STATE_EXPANDED;
            mPayDetailsView.setAlpha(1);
            mSuccessView.setAlpha(0);
            mAuthorizeButton.setEnabled(true);
            mAuthCancelButton.setEnabled(true);
            mAuthXButton.setEnabled(true);
        } else {
            state = BottomSheetBehavior.STATE_COLLAPSED;
            mCredentialCardView.resetCard();
        }
        bottomSheetBehavior.setState(state);
    }

    private void showSuccess(boolean show) {
        if(show) {
            mAuthCancelButton.setEnabled(false);
            mAuthXButton.setEnabled(false);
            animateAlpha(mPayDetailsView, 0, getResources().getInteger(R.integer.alphaFadeDuration));

            final Runnable showSuccessView = new Runnable() {
                @Override
                public void run() {
                    animateAlpha(mSuccessView, 1, getResources().getInteger(R.integer.alphaFadeDuration));
                }
            };
            final Runnable finishTransaction = new Runnable() {
                @Override
                public void run() {
                    showAuthorizationSheet(false);
                }
            };
            new Handler().postDelayed(showSuccessView, getResources().getInteger(R.integer.alphaFadeDuration));
            new Handler().postDelayed(finishTransaction, 3000);

        } else {
            showAuthorizationSheet(false);
        }
    }

    @Override
    public void onCardInserted() {
        mRings.stopAnimation();
        animateAlpha(mRings, 1, getResources().getInteger(R.integer.alphaFadeDuration));
        mRings.startAnimation();
        animateAlpha(mCancelButton, 1, getResources().getInteger(R.integer.alphaFadeDuration));
        mAwaitingAuthSheetHandler = new Handler();
        mAwaitingAuthSheetHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateAlpha(mRings, 0, getResources().getInteger(R.integer.alphaFadeDuration));
                showAuthorizationSheet(true);
            }
        }, 2500);
    }

    private void animateAlpha(View view, float alpha, long duration) {
        view.animate()
                .alpha(alpha)
                .setDuration(duration)
                .start();
    }
}