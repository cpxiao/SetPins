package com.cpxiao.setpins.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.ThreadUtils;
import com.cpxiao.setpins.imps.OnTwoPlayerGameListener;
import com.cpxiao.setpins.mode.LevelData;
import com.cpxiao.setpins.views.GameViewWith2Player;
import com.cpxiao.setpins.views.RotateTextView;
import com.cpxiao.gamelib.fragment.BaseFragment;

/**
 * @author cpxiao on 2017/09/02.
 */

public class TwoPlayerGameFragment extends BaseFragment implements OnTwoPlayerGameListener {
    /**
     * 多少分一局
     */
    private static final int SCORE_OF_END = 7;
    private FrameLayout mGameViewLayout;

    private int mScoreTop = 0, mScoreBottom = 0;
    private RotateTextView mScoreView;
    private RotateTextView mTopPlayerMsgView;
    private RotateTextView mBottomPlayerMsgView;

    public static TwoPlayerGameFragment newInstance(Bundle bundle) {
        TwoPlayerGameFragment fragment = new TwoPlayerGameFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mTopPlayerMsgView = (RotateTextView) view.findViewById(R.id.top_player_msg);
        mTopPlayerMsgView.setText("");
        mBottomPlayerMsgView = (RotateTextView) view.findViewById(R.id.bottom_player_msg);
        mBottomPlayerMsgView.setText("");

        mScoreView = (RotateTextView) view.findViewById(R.id.score_text_view);
        setScore();

        mGameViewLayout = (FrameLayout) view.findViewById(R.id.game_view_layout);
        initGameView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_2player;
    }

    private void setScore() {
        String text = mScoreTop + " : " + mScoreBottom;
        mScoreView.setText(text);
    }

    private void initGameView() {
        Context context = getHoldingActivity();
        mGameViewLayout.removeAllViews();
        LevelData data = LevelData.getRandomData(0);
        GameViewWith2Player view = new GameViewWith2Player(context, data);
        view.setOnTwoPlayerGameListener(this);
        mGameViewLayout.addView(view);
    }


    @Override
    public void onBottomPlayerWin() {
        mScoreBottom++;
        checkGameOver();
    }

    @Override
    public void onTopPlayerWin() {
        mScoreTop++;
        checkGameOver();
    }

    private void checkGameOver() {
        ThreadUtils.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setScore();
                if (mScoreTop >= SCORE_OF_END) {
                    mTopPlayerMsgView.setText(R.string.you_win);
                    mBottomPlayerMsgView.setText(R.string.you_lose);
                    return;
                }
                if (mScoreBottom >= SCORE_OF_END) {
                    mTopPlayerMsgView.setText(R.string.you_lose);
                    mBottomPlayerMsgView.setText(R.string.you_win);
                    return;
                }
                // 生成新game View
                initGameView();
            }
        });


    }
}
