package com.marc.nelnet.nelnetpayexperience.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.marc.nelnet.nelnetpayexperience.R;
import com.marc.nelnet.nelnetpayexperience.customviews.CredentialCardView;
import com.marc.nelnet.nelnetpayexperience.customviews.RippleRings;
import com.marc.nelnet.nelnetpayexperience.fragments.AuthorizePayDialogFragment;
import com.marc.nelnet.nelnetpayexperience.fragments.BaseFragment;
import com.marc.nelnet.nelnetpayexperience.fragments.PayFragment;

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
//        bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }



    @Override
    protected String getScreenTitle() {
        if(mCurrentFragment == null) {
            return "";
        }
        return mCurrentFragment.getScreenName();
    }
}
