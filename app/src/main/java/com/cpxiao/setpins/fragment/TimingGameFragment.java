package com.cpxiao.setpins.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.androidutils.library.utils.ThreadUtils;
import com.cpxiao.setpins.imps.OnGameListener;
import com.cpxiao.setpins.mode.LevelData;
import com.cpxiao.setpins.mode.extra.Extra;
import com.cpxiao.setpins.views.CountDownTextView;
import com.cpxiao.setpins.views.GameViewWith1Player;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;
import com.cpxiao.zads.core.ZAdPosition;

/**
 * @author cpxiao on 2017/09/02.
 */

public class TimingGameFragment extends BaseZAdsFragment implements OnGameListener {
    private FrameLayout mGameViewLayout;
    private CountDownTextView mCountDownTextView;
    private TextView mScoreTextView;
    private TextView mBestScoreTextView;
    private int mScore = 0;
    private boolean isDialogShown = false;//是否显示了dialog，防止game over之后又time up弹出dialog

    public static TimingGameFragment newInstance(Bundle bundle) {
        TimingGameFragment fragment = new TimingGameFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        loadZAds(ZAdPosition.POSITION_BEST_SCORE);

        final Context context = getHoldingActivity();
        Button titleBarLeftBtn = (Button) view.findViewById(R.id.title_bar_left_btn);
        titleBarLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        TextView titleBarTitle = (TextView) view.findViewById(R.id.title_bar_title);
        titleBarTitle.setText(R.string.btn_time);

        mScoreTextView = (TextView) view.findViewById(R.id.score);
        mBestScoreTextView = (TextView) view.findViewById(R.id.best_score);
        setScoreAndBestScore(context, 0);

        mCountDownTextView = (CountDownTextView) view.findViewById(R.id.count_down_text_view);

        mGameViewLayout = (FrameLayout) view.findViewById(R.id.game_view_layout);


        setup(context);


    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_timing;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) {
            Log.d(TAG, "onDestroy: ...");
        }
        super.onDestroy();
        //取消倒计时
        mCountDownTextView.setOnTimeUpListener(null);
        mCountDownTextView.destroy();
    }

    private void setupCountDownTextView(final Context context) {
        mCountDownTextView.resetTime(30000);
        mCountDownTextView.setOnTimeUpListener(new CountDownTextView.OnTimeUpListener() {
            @Override
            public void timeUp() {
                // 跳转至结果页
                String title = getString(R.string.time_up);
                String msg = getString(R.string.score) + ": " + mScore;
                showDialog(context, title, msg);
            }
        });
    }

    private void setup(Context context) {
        isDialogShown = false;
        setupCountDownTextView(context);
        setupGameView();
    }


    private void setupGameView() {
        Context context = getHoldingActivity();
        LevelData data = LevelData.getRandomData(mScore);
        GameViewWith1Player view = new GameViewWith1Player(context, data);
        view.setOnGameListener(this);
        mGameViewLayout.removeAllViews();
        mGameViewLayout.addView(view);
    }


    private void setScoreAndBestScore(Context context, int score) {
        int bestScore = PreferencesUtils.getInt(context, Extra.Key.BEST_SCORE, Extra.Key.BEST_SCORE_DEFAULT);
        if (score > bestScore) {
            PreferencesUtils.putInt(context, Extra.Key.BEST_SCORE, score);
            bestScore = score;
        }
        String best = getString(R.string.btn_best_score) + ": " + bestScore;
        mBestScoreTextView.setText(best);
        mScoreTextView.setText(String.valueOf(score));
    }


    @Override
    public void onGameStart() {
        if (DEBUG) {
            Log.d(TAG, "onGameStart: ");
        }
        mCountDownTextView.start();
    }

    @Override
    public void onGameOver() {
        if (DEBUG) {
            Log.d(TAG, "onGameOver: ");
        }
        mCountDownTextView.pause();

        final Context context = getHoldingActivity();
        ThreadUtils.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 跳转至结果页
                String title = getString(R.string.fail);
                String msg = getString(R.string.try_again) + " ?";
                showDialog(context, title, msg);
            }
        });
    }

    @Override
    public void onSuccess() {
        if (DEBUG) {
            Log.d(TAG, "onSuccess: ");
        }
        final Context context = getHoldingActivity();
        ThreadUtils.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScore++;
                setScoreAndBestScore(context, mScore);
                // 生成新game View
                setupGameView();
            }
        });
    }

    private void showDialog(final Context context, final String title, final String msg) {
        if (isDialogShown) {
            return;
        }
        isDialogShown = true;
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
                                setup(context);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO return to home
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
