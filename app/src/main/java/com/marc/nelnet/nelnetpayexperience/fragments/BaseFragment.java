package com.marc.nelnet.nelnetpayexperience.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by Marc on 3/8/2017.
 */

public abstract class BaseFragment extends Fragment {
    public abstract String getScreenName();
    public void qrScannerResultsOk() {

    }

    public void qrScannerResultsFailed() {

    }
}
