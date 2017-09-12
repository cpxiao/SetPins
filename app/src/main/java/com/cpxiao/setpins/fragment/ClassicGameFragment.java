package com.cpxiao.setpins.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.androidutils.library.utils.ThreadUtils;
import com.cpxiao.setpins.imps.OnGameListener;
import com.cpxiao.setpins.mode.LevelData;
import com.cpxiao.setpins.mode.extra.Extra;
import com.cpxiao.setpins.views.GameViewWith1Player;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;
import com.cpxiao.zads.core.ZAdPosition;

/**
 * @author cpxiao on 2017/09/02.
 */

public class ClassicGameFragment extends BaseZAdsFragment implements OnGameListener {
    private int mLevel = Extra.Key.LEVEL_DEFAULT;

    private TextView mLevelTitleTV;
    private LinearLayout layout;

    public static ClassicGameFragment newInstance(Bundle bundle) {
        ClassicGameFragment fragment = new ClassicGameFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        loadZAds(ZAdPosition.POSITION_GAME);

        Bundle bundle = getArguments();
        Context context = getHoldingActivity();
        if (bundle != null) {
            mLevel = bundle.getInt(Extra.Key.LEVEL, Extra.Key.LEVEL_DEFAULT);
        }
        Button titleBarLeftBtn = (Button) view.findViewById(R.id.title_bar_left_btn);
        titleBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        TextView titleBarTitle = (TextView) view.findViewById(R.id.title_bar_title);
        titleBarTitle.setText(R.string.btn_classic);

        mLevelTitleTV = (TextView) view.findViewById(R.id.sub_title);
        layout = (LinearLayout) view.findViewById(R.id.layout_container);

        setupData(context);
    }

    private void setupData(Context context) {
        String text = getString(R.string.level) + " " + mLevel;
        mLevelTitleTV.setText(text);

        LevelData data = LevelData.getRandomData(mLevel);
        GameViewWith1Player gameViewWith1Player = new GameViewWith1Player(context, data);
        gameViewWith1Player.setOnGameListener(this);
        layout.removeAllViews();
        layout.addView(gameViewWith1Player);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_classic;
    }

    @Override
    public void onGameStart() {
        //do nothing
    }

    @Override
    public void onGameOver() {
        Context context = getHoldingActivity();
        String title = getString(R.string.you_lose);
        String msg = getString(R.string.try_again) + " ?";
        showDialog(context, title, msg);
    }

    @Override
    public void onSuccess() {
        final Context context = getHoldingActivity();
        int level = PreferencesUtils.getInt(context, Extra.Key.LEVEL, Extra.Key.LEVEL_DEFAULT);
        if (level <= mLevel) {
            PreferencesUtils.putInt(context, Extra.Key.LEVEL, mLevel);
        }

        mLevel++;
        ThreadUtils.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupData(context);
            }
        });
    }


    private void showDialog(final Context context, final String title, final String msg) {
        ThreadUtils.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // restart
                                setupData(context);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // return to home
                                removeFragment();
                            }
                        })
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        });

    }
}
