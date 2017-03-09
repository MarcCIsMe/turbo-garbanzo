package com.marc.nelnet.nelnetpayexperience.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.marc.nelnet.nelnetpayexperience.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBarStyle();
    }

    protected void setActionBarStyle() {
        final ActionBar supportActionBar = getSupportActionBar();
        //supportActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background, R.style.AppTheme));
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.action_bar_title);
        textviewTitle.setText(getScreenTitle());
        supportActionBar.setCustomView(viewActionBar, params);
        supportActionBar.setDisplayShowCustomEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setIcon(R.color.transparent);
        supportActionBar.setHomeButtonEnabled(true);
    }

    protected abstract String getScreenTitle();
}
