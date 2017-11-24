package com.cpxiao.setpins.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cpxiao.R;
import com.cpxiao.setpins.imps.OnTwoPlayerGameListener;
import com.cpxiao.setpins.mode.Circle;
import com.cpxiao.setpins.mode.LevelData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GameViewWith2Player
 *
 * @author cpxiao on 2017/6/6.
 */
public class GameViewWith2Player extends GameView implements View.OnTouchListener {
    private ConcurrentLinkedQueue<Circle> mTopPlayerCircleList;
    private ConcurrentLinkedQueue<Circle> mBottomPlayerCircleList;

    protected static final Paint mTopPlayerCirclePaint = new Paint();
    protected static final Paint mBottomPlayerCirclePaint = new Paint();
    /**
     * 默认待插入针数量的最小值
     */
    private static final int PLAYER_CIRCLES_NUMBER_DEFAULT = 3;
    private int mPlayerCirclesNumber = PLAYER_CIRCLES_NUMBER_DEFAULT;
    /**
     * 待插入圆半径加上之间的间隔占半径比例
     */
    private static final float R_PERCENT = 1.5f;

    private OnTwoPlayerGameListener mOnTwoPlayerGameListener;

    private static final int GAME_STATE_NORMAL = 0;
    private static final int GAME_STATE_TOP_PLAYER_WIN = 1;
    private static final int GAME_STATE_BOTTOM_PLAYER_WIN = 2;
    private int mGameState = GAME_STATE_NORMAL;

    protected static final Paint mAnimationTopArcPaint = new Paint();
    protected static final Paint mAnimationBottomArcPaint = new Paint();

    static {
        mTopPlayerCirclePaint.setAntiAlias(true);//抗锯齿
//        mTopPlayerCirclePaint.setDither(true);//防抖动

        mBottomPlayerCirclePaint.setAntiAlias(true);//抗锯齿
//        mBottomPlayerCirclePaint.setDither(true);//防抖动

        mAnimationTopArcPaint.setAntiAlias(true);//抗锯齿
//        mAnimationTopArcPaint.setDither(true);//防抖动

        mAnimationBottomArcPaint.setAntiAlias(true);//抗锯齿
//        mAnimationBottomArcPaint.setDither(true);//防抖动

    }

    public GameViewWith2Player(Context context) {
        super(context);
    }

    public GameViewWith2Player(Context context, LevelData data) {
        super(context, "", data.rotateSpeed, data.baseCirclesNumber);
        this.mPlayerCirclesNumber = data.playerCirclesNumber;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        if (mPlayerCirclesNumber <= 0) {
            mPlayerCirclesNumber = PLAYER_CIRCLES_NUMBER_DEFAULT;
        }
        setBgColor(ContextCompat.getColor(getContext().getApplicationContext(), R.color.common_bg));
        mTopPlayerCircleList = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < mPlayerCirclesNumber; i++) {
            float r = mCenterCircle.smallCircleR;
            float cX = mCenterCircle.centerX;
            float cY = mCenterCircle.centerY - mCenterCircle.outerR - mCenterCircle.smallCircleR * 2 - mCenterCircle.smallCircleR * (i + 1) * (R_PERCENT + 1);
            Circle c = new Circle(cX, cY, r, null, ContextCompat.getColor(getContext(), R.color.color_2player_top));
            mTopPlayerCircleList.add(c);
        }

        mBottomPlayerCircleList = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < mPlayerCirclesNumber; i++) {
            float r = mCenterCircle.smallCircleR;
            float cX = mCenterCircle.centerX;
            float cY = mCenterCircle.centerY + mCenterCircle.outerR + mCenterCircle.smallCircleR * 2 + mCenterCircle.smallCircleR * (i + 1) * (R_PERCENT + 1);
            Circle c = new Circle(cX, cY, r, null, ContextCompat.getColor(getContext(), R.color.color_2player_bottom));
            mBottomPlayerCircleList.add(c);
        }

        mTopPlayerCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_2player_top));
        mBottomPlayerCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_2player_bottom));
        mAnimationRect = new RectF(mCenterCircle.centerX, mCenterCircle.centerY, mCenterCircle.centerX, mCenterCircle.centerY);

        setOnTouchListener(this);
    }

    /**
     * 成功或失败的过渡动画时间
     */
    private int animation_time = 1000;
    private RectF mAnimationRect;

    @Override
    protected void timingLogic() {
        if (animation_time < 0) {
            return;
        }
        if (mGameState != GAME_STATE_NORMAL) {
            mAnimationRect.left -= mCenterCircle.innerR;
            mAnimationRect.right += mCenterCircle.innerR;
            mAnimationRect.top -= mCenterCircle.innerR;
            mAnimationRect.bottom += mCenterCircle.innerR;

            setOnClickListener(null);
            animation_time -= 1000 / mFPS;

            if (mGameState == GAME_STATE_TOP_PLAYER_WIN) {
                mAnimationTopArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_success));
                mAnimationBottomArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_fail));
                if (animation_time < 0 && mOnTwoPlayerGameListener != null) {
                    mOnTwoPlayerGameListener.onTopPlayerWin();
                }
            } else if (mGameState == GAME_STATE_BOTTOM_PLAYER_WIN) {
                mAnimationTopArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_fail));
                mAnimationBottomArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_success));

                if (animation_time < 0 && mOnTwoPlayerGameListener != null) {
                    mOnTwoPlayerGameListener.onBottomPlayerWin();
                }
            }
        } else {
            super.timingLogic();
        }
    }

    @Override
    public void drawCache() {
        mCanvasCache.drawArc(mAnimationRect, 180, 180, true, mAnimationTopArcPaint);
        mCanvasCache.drawArc(mAnimationRect, 0, 180, true, mAnimationBottomArcPaint);

        super.drawCache();
        if (mBottomPlayerCircleList != null && mBottomPlayerCircleList.size() > 0) {
            for (Circle c : mBottomPlayerCircleList) {
                c.draw(mCanvasCache, mBottomPlayerCirclePaint, null);
            }
        }
        if (mTopPlayerCircleList != null && mTopPlayerCircleList.size() > 0) {
            for (Circle c : mTopPlayerCircleList) {
                c.draw(mCanvasCache, mTopPlayerCirclePaint, null);
            }
        }
    }

    private void updateBottomPlayerCircleList() {
        if (mBottomPlayerCircleList == null || mBottomPlayerCircleList.size() <= 0) {
            return;
        }
        for (Circle c : mBottomPlayerCircleList) {
            c.cY -= mCenterCircle.smallCircleR * (R_PERCENT + 1);
        }
    }

    private void updateTopPlayerCircleList() {
        if (mTopPlayerCircleList == null || mTopPlayerCircleList.size() <= 0) {
            return;
        }
        for (Circle c : mTopPlayerCircleList) {
            c.cY += mCenterCircle.smallCircleR * (R_PERCENT + 1);
        }
    }

    public void setOnTwoPlayerGameListener(OnTwoPlayerGameListener listener) {
        mOnTwoPlayerGameListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (DEBUG) {
            Log.d(TAG, "onTouch: " + event.getAction());
        }
        synchronized (mSurfaceHolder.getSurface()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mGameState != GAME_STATE_NORMAL) {
                    return true;
                }
                float y = event.getY();
                if (y > mViewHeight / 2) {
                    //click bottom
                    if (mBottomPlayerCircleList != null && mBottomPlayerCircleList.size() > 0) {
                        mCenterCircle.addSmallCircle(90, null, mBottomPlayerCircleList.poll().color);

                        updateBottomPlayerCircleList();

                        if (!mCenterCircle.canBePlaced()) {
                            // bottom player game over
                            mGameState = GAME_STATE_TOP_PLAYER_WIN;
                            //此处一定要返回,否则在最后一个插入时失败,会game over 之后再success
                            return true;
                        }
                    }
                    //判断全部大头针已插入
                    if (mBottomPlayerCircleList == null || mBottomPlayerCircleList.size() <= 0) {
                        // bottom player success
                        mGameState = GAME_STATE_BOTTOM_PLAYER_WIN;
                    }
                } else {
                    //click top
                    if (mTopPlayerCircleList != null && mTopPlayerCircleList.size() > 0) {
                        mCenterCircle.addSmallCircle(270, null, mTopPlayerCircleList.poll().color);

                        updateTopPlayerCircleList();

                        if (!mCenterCircle.canBePlaced()) {
                            // top player game over
                            mGameState = GAME_STATE_BOTTOM_PLAYER_WIN;
                            //此处一定要返回,否则在最后一个插入时失败,会game over 之后再success
                            return true;
                        }
                    }
                    //判断全部大头针已插入
                    if (mTopPlayerCircleList == null || mTopPlayerCircleList.size() <= 0) {
                        // top player success
                        mGameState = GAME_STATE_TOP_PLAYER_WIN;
                    }
                }
            }
            return true;
        }
    }
}
