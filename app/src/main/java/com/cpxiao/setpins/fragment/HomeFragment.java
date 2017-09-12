package com.cpxiao.setpins.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.setpins.mode.extra.Extra;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;
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
        classic.setOnClickListener(this);

        Button time = (Button) view.findViewById(R.id.btn_time);
        time.setOnClickListener(this);

        Button twoPlayers = (Button) view.findViewById(R.id.btn_two_players);
        twoPlayers.setOnClickListener(this);

        Button settings = (Button) view.findViewById(R.id.btn_settings);
        settings.setVisibility(View.GONE);

        Button quit = (Button) view.findViewById(R.id.btn_quit);
        quit.setOnClickListener(this);
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
        } else if (id == R.id.btn_quit) {
            removeFragment();
        }
    }
}
