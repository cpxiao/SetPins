package com.cpxiao.setpins.mode;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.cpxiao.androidutils.library.utils.StringUtils;

/**
 * Circle
 *
 * @author cpxiao on 2017/6/6.
 */

public class Circle {
    public float cX;
    public float cY;
    public float r;
    public String text;
    public int color = -1;

    private Circle() {

    }

    public Circle(float cX, float cY, float r, String text, int color) {
        this.cX = cX;
        this.cY = cY;
        this.r = r;
        this.text = text;
        this.color = color;
    }

    public void draw(Canvas canvas, Paint circlePaint, Paint textPaint) {
        if (color != -1) {
            circlePaint.setColor(color);
        }
        canvas.drawCircle(cX, cY, r, circlePaint);
        if (!StringUtils.isEmpty(text) && textPaint != null) {
            //绘制数字，稍微下移一点
            canvas.drawText(text, cX, cY + r * 0.3f, textPaint);
        }
    }

}