package com.cpxiao.setpins.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.cpxiao.R;
import com.cpxiao.setpins.mode.CenterCircle;
import com.cpxiao.gamelib.view.BaseSurfaceViewFPS;

/**
 * GameView
 * 中心View，包含中心大圆，已插入大头针
 *
 * @author cpxiao on 2017/6/6.
 */
public class GameView extends BaseSurfaceViewFPS {

    protected int rotateSpeed = 90;
    protected String centerText = null;
    protected int baseSmallCircleCount = 5;
    protected CenterCircle mCenterCircle;

    protected static final Paint mCenterCirclePaint = new Paint();
    protected static final Paint mCenterCircleTextPaint = new Paint();
    protected static final Paint mLinePaint = new Paint();
    protected static final Paint mSmallCirclePaint = new Paint();
    protected static final Paint mSmallCircleTextPaint = new Paint();

    protected int mDefaultColor;

    static {
        mCenterCirclePaint.setAntiAlias(true);//抗锯齿
        mCenterCirclePaint.setDither(true);//防抖动

        mCenterCircleTextPaint.setAntiAlias(true);//抗锯齿
        mCenterCircleTextPaint.setDither(true);//防抖动
        mCenterCircleTextPaint.setStrokeWidth(3);
        mCenterCircleTextPaint.setTextAlign(Paint.Align.CENTER);
        mCenterCircleTextPaint.setColor(Color.WHITE);

        mLinePaint.setAntiAlias(true);//抗锯齿
        mLinePaint.setDither(true);//防抖动
        mLinePaint.setStrokeWidth(5);

        mSmallCirclePaint.setAntiAlias(true);//抗锯齿
        mSmallCirclePaint.setDither(true);//防抖动

        mSmallCircleTextPaint.setAntiAlias(true);//抗锯齿
        mSmallCircleTextPaint.setDither(true);//防抖动
        mSmallCircleTextPaint.setStrokeWidth(3);
        mSmallCircleTextPaint.setTextAlign(Paint.Align.CENTER);
        mSmallCircleTextPaint.setColor(Color.WHITE);
    }

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, String centerTest, int rotateSpeed, int baseCirclesNumber) {
        super(context);
        this.centerText = centerTest;
        this.rotateSpeed = rotateSpeed;
        this.baseSmallCircleCount = baseCirclesNumber;
    }

    @Override
    protected void timingLogic() {
        if (mCenterCircle != null) {
            mCenterCircle.update();
        }
    }

    @Override
    protected void initWidget() {
        mDefaultColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.common_black);

        float innerR = 0.25f * mViewLength / 2;
        float outer = 0.7f * mViewLength / 2;
        if (Math.abs(rotateSpeed) < 60) {
            rotateSpeed = 90;
        }
        mCenterCircle = new CenterCircle(mViewWidth / 2, mViewHeight / 2, centerText,
                innerR, outer, rotateSpeed, baseSmallCircleCount, mDefaultColor);

        mCenterCircleTextPaint.setTextSize(innerR);
        mSmallCircleTextPaint.setTextSize(mCenterCircle.smallCircleR);

    }

    @Override
    public void drawCache() {
        if (mCenterCircle != null) {
            mCenterCircle.draw(mCanvasCache, mCenterCirclePaint, mCenterCircleTextPaint,
                    mLinePaint, mSmallCirclePaint, mSmallCircleTextPaint);
        }
    }


}
