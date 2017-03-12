package com.marc.nelnet.nelnetpayexperience.fragments;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Outline;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.percent.PercentRelativeLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.marc.nelnet.nelnetpayexperience.utils.AuthorizationSheetBehavior;
import com.marc.nelnet.nelnetpayexperience.utils.QRCamera;
import com.marc.nelnet.nelnetpayexperience.R;
import com.marc.nelnet.nelnetpayexperience.customviews.CredentialCardView;
import com.marc.nelnet.nelnetpayexperience.customviews.RingRippleController;

import static android.view.View.GONE;

/**
 * Created by Marc on 3/8/2017.
 */

public class PayFragment extends BaseFragment implements CredentialCardView.CredentialCardViewListener, QRCamera.QRCameraListener {
    private CredentialCardView mCredentialCardView;
    private RingRippleController mRings;
    private Button mCancelButton;
    private PercentRelativeLayout mAuthorizeView;
    private Handler mAwaitingAuthSheetHandler;
    private View mPayDetailsView;
    private View mSuccessView;
    private Button mAuthorizeButton;
    private Button mAuthCancelButton;
    private ImageView mAuthXButton;
    private boolean mBottomSheetUp = false;
    private SurfaceView mQRSurfaceView;
    private View mMainLayout;
    private boolean mCardInserted = false;
    private AuthorizationSheetBehavior mBottomSheetBehavior;

    public static PayFragment newInstance() {
        PayFragment fragment = new PayFragment();
        return fragment;
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
        mMainLayout = getActivity().findViewById(R.id.main_layout);
        mRings = (RingRippleController)getActivity().findViewById(R.id.rings);
        mAuthorizeView = (PercentRelativeLayout)getActivity().findViewById(R.id.bottom_sheet);
        mPayDetailsView = mAuthorizeView.findViewById(R.id.auth_pay_details);
        mSuccessView = mAuthorizeView.findViewById(R.id.auth_success);
        mQRSurfaceView = (SurfaceView)getActivity().findViewById(R.id.qr_camera_view);
        setupAuthorizeButton();
        setupAuthorizeSheetBehavior();
        setupCancelButton();

        View.OnClickListener closeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardInserted = false;
                showAuthorizationSheet(false);
                mCredentialCardView.resetCard();
                mQRSurfaceView.setVisibility(GONE);
            }
        };
        mAuthXButton = (ImageView)mAuthorizeView.findViewById(R.id.close_authorization);
        mAuthXButton.setOnClickListener(closeClickListener);
        mAuthCancelButton = (Button)mAuthorizeView.findViewById(R.id.cancel_authorization);
        mAuthCancelButton.setOnClickListener(closeClickListener);
        setupScanQRCodeButton();

    }

    private SpannableStringBuilder buildQRSpanString() {
        SpannableStringBuilder builder = new SpannableStringBuilder(getResources().getString(R.string.pay_frag_scan_qr_code));
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.secondaryGrey)), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.linkColor, null)), 3, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(R.style.SecondaryLightTextStyle), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(R.style.LinkTextStyle), 3, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private void setupAuthorizeSheetBehavior() {
        mBottomSheetBehavior = (AuthorizationSheetBehavior)BottomSheetBehavior.from(mAuthorizeView);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setupAuthorizeButton() {
        mAuthorizeButton = (Button)mAuthorizeView.findViewById(R.id.authorize_button);
        mAuthorizeButton.setClickable(true);
        mAuthorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                showSuccess(true);
            }
        });
    }

    private void setupCancelButton() {
        mCancelButton = (Button)getActivity().findViewById(R.id.cancel_pay);
        mCancelButton.setAlpha(0);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetUp) {
                    return;
                }
                mCardInserted = false;
                mAwaitingAuthSheetHandler.removeCallbacksAndMessages(null);
                animateAlpha(mCancelButton, 0, getResources().getInteger(R.integer.alphaFadeDuration));
                animateAlpha(mRings, 0, getResources().getInteger(R.integer.alphaFadeDuration));
                mCredentialCardView.resetCard();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void setupScanQRCodeButton() {
        TextView scanQRCode = (TextView)getActivity().findViewById(R.id.scan_qr_code);
        scanQRCode.setText(buildQRSpanString());
        scanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBottomSheetUp && !mCardInserted) {
                    mQRSurfaceView.setVisibility(View.VISIBLE);
                    startQRScanner();
                }
            }
        });
    }

    @Override
    public void qrScannerResultsOk() {
        startQRScanner();
    }

    @Override
    public void qrScannerResultsFailed() {
        mQRSurfaceView.setVisibility(GONE);
        mMainLayout.setVisibility(View.VISIBLE);
        animateAlpha(mMainLayout, 1, getResources().getInteger(R.integer.alphaFadeDuration));
    }

    @Override
    public void onBarcodeFound(final String barcodeText) {
        mQRSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), barcodeText, Toast.LENGTH_SHORT).show();
                showAuthorizationSheet(true);
            }
        });
    }

    private void startQRScanner() {
        animateAlpha(mMainLayout, 0, getResources().getInteger(R.integer.alphaFadeDuration));
        mQRSurfaceView.setVisibility(View.VISIBLE);
        mMainLayout.setVisibility(GONE);
        QRCamera.startQRCamera(getActivity(), mQRSurfaceView, this);
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
        mBottomSheetUp = show;
        if(show) {
            final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.info_received);
            mp.start();
            state = BottomSheetBehavior.STATE_EXPANDED;
            mPayDetailsView.setAlpha(1);
            mSuccessView.setAlpha(0);
            mAuthorizeButton.setEnabled(true);
            mAuthCancelButton.setEnabled(true);
            mAuthXButton.setEnabled(true);
        } else {
            mCardInserted = false;
            animateAlpha(mMainLayout, 1, getResources().getInteger(R.integer.alphaFadeDuration));
            mMainLayout.setVisibility(View.VISIBLE);
            state = BottomSheetBehavior.STATE_COLLAPSED;
            mCredentialCardView.resetCard();
        }
        bottomSheetBehavior.setState(state);
    }

    private void showSuccess(boolean show) {
        if(show) {
            mAuthCancelButton.setEnabled(false);
            mAuthXButton.setEnabled(false);
            mQRSurfaceView.setVisibility(View.GONE);
            animateAlpha(mPayDetailsView, 0, getResources().getInteger(R.integer.alphaFadeDuration));

            final Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };

            final Runnable showSuccessView = new Runnable() {
                @Override
                public void run() {
                    animateAlpha(mSuccessView, 1, getResources().getInteger(R.integer.alphaFadeDuration), animatorListener);
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
        mCardInserted = false;
    }

    @Override
    public void onCardInserted() {
        mCardInserted = true;
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

    private void animateAlpha(View view, float alpha, long duration, Animator.AnimatorListener listener) {
        view.animate()
                .setListener(listener )
                .alpha(alpha)
                .setDuration(duration)
                .start();
    }

    private void animateAlpha(View view, float alpha, long duration) {
        animateAlpha(view, alpha, duration, null);
    }
}