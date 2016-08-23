package com.widevision.pillreminder.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Mercury-five on 17/12/15.
 */
public class NewRectShowCaseDrawer extends StandardShowcaseDrawer {

    private static final int ALPHA_60_PERCENT = 153;
    private float height = 0, width = 0;
    private float pointx1, pointx2, pointy1, pointy2;

    public NewRectShowCaseDrawer(Resources resources, Resources.Theme theme, final View view) {
        super(resources, theme);
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        width = view.getWidth();
                        height = view.getHeight();
                        // make sure it is not called anymore
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

    }

    @Override
    public void setShowcaseColour(int color) {
        eraserPaint.setColor(color);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        eraserPaint.setAlpha(ALPHA_60_PERCENT);
        bufferCanvas.drawRect(x - (width / 2), y - (height / 2), (x + (width / 2)), y + (height / 2), eraserPaint);
        eraserPaint.setAlpha(0);
        bufferCanvas.drawRect(x - (width / 2), y - (height / 2), (x + (width / 2)), y + (height / 2), eraserPaint);
        pointx1 = x - (width / 2);
        pointx2 = x + (width / 2);
        pointy1 = y ;
        pointy2 = y+height ;

    }

    @Override
    public int getShowcaseWidth() {
        return (int) (width);
    }

    @Override
    public int getShowcaseHeight() {
        return (int) (height);
    }

    @Override
    public float getBlockedRadius() {
        return height;
    }

    @Override
    public float[] getBlockedPoints() {
        float a[] = {pointx1, pointx2, pointy1, pointy2};
        return a;
    }
}
