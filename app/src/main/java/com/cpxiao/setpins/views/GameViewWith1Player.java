package com.cpxiao.setpins.views;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.cpxiao.R;
import com.cpxiao.setpins.imps.OnGameListener;
import com.cpxiao.setpins.mode.Circle;
import com.cpxiao.setpins.mode.LevelData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GameViewWith1Player
 *
 * @author cpxiao on 2017/6/6.
 */
public class GameViewWith1Player extends GameView implements View.OnClickListener {
    /**
     * 玩家待插入小圆列表
     */
    private ConcurrentLinkedQueue<Circle> mPlayerCircleList;
    /**
     * 默认待插入针数量的最小值
     */
    private int mPlayerCircleCount = 3;
    /**
     * 玩家待插入圆之间的间隔占半径比例
     */
    private static final float R_PERCENT = 0.5f;

    private OnGameListener mOnGameListener;

    private static final int GAME_STATE_NORMAL = 0;
    private static final int GAME_STATE_SUCCESS = 1;
    private static final int GAME_STATE_GAME_OVER = 2;
    private int mGameState = GAME_STATE_NORMAL;

    protected static final Paint mAnimationCirclePaint = new Paint();


    static {
        mAnimationCirclePaint.setAntiAlias(true);//抗锯齿
        mAnimationCirclePaint.setDither(true);//防抖动

    }

    public GameViewWith1Player(Context context) {
        super(context);
    }

    public GameViewWith1Player(Context context, LevelData data) {
        super(context, String.valueOf(data.level), data.rotateSpeed, data.baseCirclesNumber);
        if (data.playerCirclesNumber > mPlayerCircleCount) {
            this.mPlayerCircleCount = data.playerCirclesNumber;
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        //把中心view向上移动一些
        mCenterCircle.centerY -= (mViewHeight - mViewLength) * 0.16;

        setBgColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.common_bg));
        //        mPlayerCircleList = new CopyOnWriteArrayList<>();
        mPlayerCircleList = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < mPlayerCircleCount; i++) {
            float r = mCenterCircle.smallCircleR;
            float cX = mCenterCircle.centerX;
            float cY = mCenterCircle.centerY + mCenterCircle.outerR + mCenterCircle.smallCircleR * 2 + mCenterCircle.smallCircleR * (i + 1) * (R_PERCENT + 2);
            String text = String.valueOf(mPlayerCircleCount - i);
            Circle c = new Circle(cX, cY, r, text, mDefaultColor);
            mPlayerCircleList.add(c);
        }

        mAnimationCircle = new Circle(mCenterCircle.centerX, mCenterCircle.centerY, mCenterCircle.innerR, null, -1);
        setOnClickListener(this);
    }

    /**
     * 成功或失败的过渡动画时间
     */
    private int animationTime = 500;
    private Circle mAnimationCircle;

    @Override
    protected void timingLogic() {
        if (animationTime < 0) {
            return;
        }
        if (mGameState == GAME_STATE_SUCCESS || mGameState == GAME_STATE_GAME_OVER) {
            setClickable(false);
            setOnClickListener(null);
            mAnimationCircle.r *= 1.2f;
            animationTime -= 1000 / mFPS;

            if (mGameState == GAME_STATE_SUCCESS) {
                mAnimationCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_success));
                if (animationTime < 0 && mOnGameListener != null) {
                    mOnGameListener.onSuccess();
                }
                //                if (mOnGameListener != null) {
                //                    mOnGameListener.onSuccess();
                //                }
            } else if (mGameState == GAME_STATE_GAME_OVER) {
                mAnimationCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_fail));
                if (animationTime < 0 && mOnGameListener != null) {
                    mOnGameListener.onGameOver();
                }
                //                if (mOnGameListener != null) {
                //                    mOnGameListener.onGameOver();
                //                }
            }
        } else {
            super.timingLogic();
        }
    }

    @Override
    public void drawCache() {
        if (mAnimationCircle != null) {
            mAnimationCircle.draw(mCanvasCache, mAnimationCirclePaint, null);
        }
        super.drawCache();
        if (mPlayerCircleList != null && mPlayerCircleList.size() > 0) {
            for (Circle c : mPlayerCircleList) {
                c.draw(mCanvasCache, mSmallCirclePaint, mSmallCircleTextPaint);
            }
        }
    }

    private boolean isStarted = false;

    @Override
    public void onClick(View v) {
        //加个锁防止timingLogic()方法导致数据错乱,初步看来有效果
        //20161030,不要锁mSurfaceHolder.getSurface(),低版本的手机会anr
        synchronized (TAG) {
            if (!isStarted && mOnGameListener != null) {
                isStarted = true;
                mOnGameListener.onGameStart();
            }

            //记录一下坑0：纠结了半天，插入应在判断之后，否则一直game over
            //记录一下坑1:不能先判断再插入,可能判断成功后又调用了timingLogic(),导致位置发生变化,再插入有可能会重合
            if (mPlayerCircleList != null && mPlayerCircleList.size() > 0) {
                //                mCenterCircle.addSmallCircle(90, mPlayerCircleList.get(0).text, mDefaultColor);
                //                mPlayerCircleList.remove(0);
                mCenterCircle.addSmallCircle(90, mPlayerCircleList.poll().text, mDefaultColor);

                updatePlayerCircleList();

                if (!mCenterCircle.canBePlaced()) {
                    // game over
                    mGameState = GAME_STATE_GAME_OVER;
                    //此处一定要返回,否则在最后一个插入时失败,会game over 之后再success
                    return;
                }
            }
            //判断全部大头针已插入
            if (mPlayerCircleList == null || mPlayerCircleList.size() <= 0) {
                // success
                mGameState = GAME_STATE_SUCCESS;
            }
        }
    }

    private void updatePlayerCircleList() {
        if (mPlayerCircleList == null || mPlayerCircleList.size() <= 0) {
            return;
        }
        for (Circle c : mPlayerCircleList) {
            c.cY -= mCenterCircle.smallCircleR * (R_PERCENT + 2);
        }
    }

    public void setOnGameListener(OnGameListener listener) {
        mOnGameListener = listener;
    }
}
