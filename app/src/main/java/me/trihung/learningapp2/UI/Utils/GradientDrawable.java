// GradientDrawable.java
package me.trihung.learningapp2.UI.Utils;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class GradientDrawable extends Drawable {
    public enum Orientation {
        TOP_BOTTOM,
        TR_BL,
        RIGHT_LEFT,
        BR_TL,
        BOTTOM_TOP,
        BL_TR,
        LEFT_RIGHT,
        TL_BR
    }

    private final Paint mPaint;
    private final Orientation mOrientation;
    private final int[] mColors;
    private final Path mPath;

    public GradientDrawable(Orientation orientation, int[] colors) {
        mOrientation = orientation;
        mColors = colors;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
    }

    @Override
    public void draw(Canvas canvas) {
        float x0, y0, x1, y1;
        int width = getBounds().width();
        int height = getBounds().height();

        switch (mOrientation) {
            case TOP_BOTTOM:
                x0 = 0; y0 = 0; x1 = 0; y1 = height;
                break;
            case TR_BL:
                x0 = width; y0 = 0; x1 = 0; y1 = height;
                break;
            case RIGHT_LEFT:
                x0 = width; y0 = 0; x1 = 0; y1 = 0;
                break;
            case BR_TL:
                x0 = width; y0 = height; x1 = 0; y1 = 0;
                break;
            case BOTTOM_TOP:
                x0 = 0; y0 = height; x1 = 0; y1 = 0;
                break;
            case BL_TR:
                x0 = 0; y0 = height; x1 = width; y1 = 0;
                break;
            case LEFT_RIGHT:
                x0 = 0; y0 = 0; x1 = width; y1 = 0;
                break;
            case TL_BR:
            default:
                x0 = 0; y0 = 0; x1 = width; y1 = height;
                break;
        }

        mPaint.setShader(new LinearGradient(
                x0, y0, x1, y1, mColors, null, Shader.TileMode.CLAMP
        ));

        mPath.reset();
        mPath.addRect(0, 0, width, height, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}