package com.cpxiao.setpins.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.androidutils.library.utils.RateAppUtils;
import com.cpxiao.androidutils.library.utils.ShareAppUtils;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;
import com.cpxiao.setpins.mode.extra.Extra;
import com.cpxiao.zads.core.ZAdPosition;

/**
 * @author cpxiao on 2017/09/02.
 */

public class HomeFragment extends BaseZAdsFragment implements View.OnClickListener {

    public static HomeFragment newInstance(Bundle bundle) {
        HomeFragment fragment = new HomeFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        loadZAds(ZAdPosition.POSITION_HOME);

        Button classic = (Button) view.findViewById(R.id.btn_classic);
        Button time = (Button) view.findViewById(R.id.btn_time);
        Button twoPlayers = (Button) view.findViewById(R.id.btn_two_players);

        ImageButton rateApp = (ImageButton) view.findViewById(R.id.rate_app);
        ImageButton share = (ImageButton) view.findViewById(R.id.share);
        ImageButton bestScore = (ImageButton) view.findViewById(R.id.best_score);
        ImageButton settings = (ImageButton) view.findViewById(R.id.settings);

        classic.setOnClickListener(this);
        time.setOnClickListener(this);
        twoPlayers.setOnClickListener(this);

        rateApp.setOnClickListener(this);
        share.setOnClickListener(this);
        bestScore.setOnClickListener(this);
        settings.setOnClickListener(this);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Context context = getHoldingActivity();
        if (id == R.id.btn_classic) {
            int level = PreferencesUtils.getInt(context, Extra.Key.LEVEL, Extra.Key.LEVEL_DEFAULT);
            Bundle bundle = new Bundle();
            bundle.putInt(Extra.Key.LEVEL, level);
            addFragment(ClassicGameFragment.newInstance(bundle));
        } else if (id == R.id.btn_time) {
            addFragment(TimingGameFragment.newInstance(null));
        } else if (id == R.id.btn_two_players) {
            addFragment(TwoPlayerGameFragment.newInstance(null));
        } else if (id == R.id.rate_app) {
            RateAppUtils.rate(context);
        } else if (id == R.id.share) {
            String msg = getString(R.string.share_msg) + "\n" +
                    getString(R.string.app_name) + "\n" +
                    "https://play.google.com/store/apps/details?id=" + context.getPackageName();
            ShareAppUtils.share(context, getString(R.string.share), msg);
        } else if (id == R.id.best_score) {
            Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.settings) {
            Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
        }
    }
}
