package com.marc.nelnet.nelnetpayexperience.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;


import com.marc.nelnet.nelnetpayexperience.R;
import com.marc.nelnet.nelnetpayexperience.fragments.BaseFragment;
import com.marc.nelnet.nelnetpayexperience.fragments.PayFragment;

import static com.marc.nelnet.nelnetpayexperience.utils.QRCamera.CAMERA_REQUEST_CODE;

/**
 * Created by Marc on 3/8/2017.
 */

public class MainActivity extends BaseActivity {

    private BaseFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                BaseFragment fragment = null;
                switch (item.getItemId())
                {
                    case R.id.action_pay:
                        fragment = PayFragment.newInstance();
                        break;
                }
                if(fragment == null) {
                    return true;
                }
                mCurrentFragment = fragment;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, mCurrentFragment);
                ft.commit();
                setActionBarStyle();
                return true;
            }
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mCurrentFragment = PayFragment.newInstance();
        ft.replace(R.id.fragment, mCurrentFragment);
        ft.commit();
        setActionBarStyle();
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCurrentFragment.qrScannerResultsOk();
            } else {
                mCurrentFragment.qrScannerResultsFailed();
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage(R.string.qr_code_rationale_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                                }
                            })
                            .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mCurrentFragment.qrScannerResultsFailed();
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
    }

    @Override
    protected String getScreenTitle() {
        if(mCurrentFragment == null) {
            return "";
        }
        return mCurrentFragment.getScreenName();
    }
}
