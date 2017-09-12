package com.cpxiao.setpins.mode;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.cpxiao.androidutils.library.utils.StringUtils;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CenterCircle
 *
 * @author cpxiao on 2017/6/6.
 */

public class CenterCircle {
    private static final String TAG = CenterCircle.class.getSimpleName();

    private static final int mFPS = 30;
    //两枚针角度差的最小角度值
    private static final int SMALL_CIRCLE_MIN_ANGLE = 12;
    //中心坐标x
    public float centerX;
    //中心坐标y
    public float centerY;
    //中心文字
    public String centerText;
    //中间小圆的半径
    public float innerR;
    //中间大圆的半径
    public float outerR;
    //外部小圆的半径
    public float smallCircleR;
    //旋转速度
    public int rotateSpeed;

    //外部小圆列表
    public CopyOnWriteArrayList<SmallCircle> mSmallCircleList = null;

    public int mDefaultColor;

    private CenterCircle() {

    }

    public CenterCircle(float centerX, float centerY, String centerText,
                        float innerR, float outerR, int rotateSpeed,
                        int smallCircleCount, int defaultColor) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerText = centerText;
        this.innerR = innerR;
        this.outerR = outerR;
        this.rotateSpeed = rotateSpeed;
        this.mDefaultColor = defaultColor;
        smallCircleR = (float) (outerR * Math.sin(Math.PI * SMALL_CIRCLE_MIN_ANGLE / 360));
        initSmallCircleList(smallCircleCount, smallCircleR);
    }

    private void initSmallCircleList(int smallCircleCount, float r) {
        if (smallCircleCount <= 0) {
            smallCircleCount = 3;
        }
        int deltaAngle = 360 / smallCircleCount;
        mSmallCircleList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < smallCircleCount; i++) {
            SmallCircle smallCircle = new SmallCircle(deltaAngle * i, mDefaultColor);
            mSmallCircleList.add(smallCircle);
        }
    }

    /**
     * 判断是否可放置
     *
     * @return boolean
     */
    public boolean canBePlaced() {
        if (mSmallCircleList == null || mSmallCircleList.size() <= 0) {
            return true;
        }
        for (int i = 0; i < mSmallCircleList.size(); i++) {
            for (int j = 0; j < mSmallCircleList.size(); j++) {
                if (i == j) {
                    continue;
                }
                int angle0 = mSmallCircleList.get(i).angle;
                int angle1 = mSmallCircleList.get(j).angle;
                //游戏结束的逻辑判断
                if ((Math.abs(angle0 - angle1) <= SMALL_CIRCLE_MIN_ANGLE) ||
                        ((Math.abs(angle0 - angle1 - 360) <= SMALL_CIRCLE_MIN_ANGLE))) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addSmallCircle(int angle, String text, int color) {
        SmallCircle smallCircle = new SmallCircle(angle, color);
        smallCircle.text = text;
        smallCircle.color = color;
        mSmallCircleList.add(smallCircle);
    }

    public void update() {
        if (mSmallCircleList != null) {
            for (SmallCircle s : mSmallCircleList) {
                s.angle = (s.angle + rotateSpeed / mFPS) % 360;
                s.computeXY();
            }
        }
    }

    /**
     * @param canvas                画布
     * @param centerCirclePaint     中心圆画笔
     * @param centerCircleTextPaint 中心文字画笔
     * @param linePaint             大头针的线画笔
     * @param smallCirclePaint      大头针圆画笔
     * @param smallCircleTextPaint  大头针文字画笔
     */
    public void draw(Canvas canvas, Paint centerCirclePaint, Paint centerCircleTextPaint,
                     Paint linePaint, Paint smallCirclePaint, Paint smallCircleTextPaint) {
        //绘制中心小圆
        canvas.drawCircle(centerX, centerY, innerR, centerCirclePaint);

        //绘制大头针
        if (mSmallCircleList != null && mSmallCircleList.size() > 0) {
            for (SmallCircle circle : mSmallCircleList) {
                canvas.drawLine(centerX, centerY, circle.cX, circle.cY, linePaint);
                circle.draw(canvas, smallCirclePaint, smallCircleTextPaint);
            }
        }

        //绘制中心圆的文字，稍微下移一点，放在最后绘制，绘制在最上层不被覆盖。
        if (!StringUtils.isEmpty(centerText) && centerCircleTextPaint != null) {
            canvas.drawText(centerText, centerX, centerY + innerR * 0.3f, centerCircleTextPaint);
        }
    }

    private class SmallCircle extends Circle {
        /**
         * 所处大圆的角度，取值范围为[0, 360)
         */
        public int angle;

        public SmallCircle(int angle, int color) {
            super(0, 0, 0, null, color);
            this.angle = angle % 360;
            this.r = smallCircleR;
            computeXY();
        }

        public void computeX() {
            this.cX = (float) (centerX + outerR * Math.cos(Math.PI * angle / 180));
        }

        public void computeY() {
            this.cY = (float) (centerY + outerR * Math.sin(Math.PI * angle / 180));
        }

        public void computeXY() {
            computeX();
            computeY();
        }
    }
}