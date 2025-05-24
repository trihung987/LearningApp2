package me.trihung.learningapp2.UI.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.trihung.learningapp2.R;
import me.trihung.learningapp2.UI.SpeakingPronounciationActivity.AudioVisualizer;

public class VoiceVisualizerView extends View implements AudioVisualizer {

    private static final int BAR_COUNT = 30;
    private static final int MIN_BAR_HEIGHT = 5;
    private static final int MAX_RANDOM = 100;

    private Paint primaryPaint;
    private Paint secondaryPaint;
    private List<Bar> bars;
    private Random random;
    private boolean isAnimating = false;
    private boolean isPlayback = false;

    private float lastAmplitude = 0;
    private long lastUpdateTime = 0;
    private Animation barAnimation;

    public VoiceVisualizerView(Context context) {
        super(context);
        init();
    }

    public VoiceVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceVisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        primaryPaint = new Paint();
        primaryPaint.setColor(getResources().getColor(R.color.lavender));
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setAntiAlias(true);

        secondaryPaint = new Paint();
        secondaryPaint.setColor(getResources().getColor(R.color.lavender_light3));
        secondaryPaint.setAlpha(100);
        secondaryPaint.setStyle(Paint.Style.FILL);
        secondaryPaint.setAntiAlias(true);

        bars = new ArrayList<>();
        random = new Random();

        barAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bar_animation);

        for (int i = 0; i < BAR_COUNT; i++) {
            bars.add(new Bar(MIN_BAR_HEIGHT));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isAnimating && !isPlayback) {
            drawIdleBars(canvas);
            return;
        }

        int width = getWidth();
        int height = getHeight();

        float barWidth = (float) width / (BAR_COUNT * 2);

        for (int i = 0; i < bars.size(); i++) {
            Bar bar = bars.get(i);

            float left = i * (barWidth * 2) + barWidth / 2;
            float barHeight = (height * bar.getHeight()) / 100f;

            // Draw main bar
            RectF rect = new RectF(
                    left,
                    (height - barHeight) / 2,
                    left + barWidth,
                    (height + barHeight) / 2
            );
            canvas.drawRoundRect(rect, barWidth / 2, barWidth / 2, primaryPaint);

            float reflectionHeight = barHeight * 0.6f;
            RectF reflectRect = new RectF(
                    left,
                    (height - reflectionHeight) / 2 + barHeight * 0.7f,
                    left + barWidth,
                    (height + reflectionHeight) / 2 + barHeight * 0.3f
            );
            canvas.drawRoundRect(reflectRect, barWidth / 2, barWidth / 2, secondaryPaint);
        }

        if (isAnimating || isPlayback) {
            updateBars();
            invalidate();
        }
    }

    private void drawIdleBars(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        float barWidth = (float) width / (BAR_COUNT * 2);
        float idleHeight = height * 0.15f;

        for (int i = 0; i < bars.size(); i++) {
            float left = i * (barWidth * 2) + barWidth / 2;

            RectF rect = new RectF(
                    left,
                    height / 2 - idleHeight / 2,
                    left + barWidth,
                    height / 2 + idleHeight / 2
            );
            canvas.drawRoundRect(rect, barWidth / 2, barWidth / 2, primaryPaint);
        }
    }

    @Override
    public void updateAmplitude(int amplitude) {
        float normalizedAmplitude = Math.min(100, (amplitude / 32767f) * 100);

        float smoothedAmplitude = (normalizedAmplitude + lastAmplitude * 2) / 3;
        lastAmplitude = smoothedAmplitude;

        long now = System.currentTimeMillis();
        if (now - lastUpdateTime > 100) { // Limit updates to every 100ms
            lastUpdateTime = now;

            for (int i = 0; i < bars.size(); i++) {
                float randomFactor = 0.7f + random.nextFloat() * 0.6f; // 0.7 to 1.3
                float height = MIN_BAR_HEIGHT + (smoothedAmplitude * randomFactor);

                // Ensure neighboring bars have similar heights (smoother appearance)
                if (i > 0) {
                    height = (height + bars.get(i-1).getHeight()) / 2;
                }

                bars.get(i).setTargetHeight(Math.min(100, height));
            }
        }

        if (isAnimating) {
            invalidate();
        }
    }

    private void updateBars() {
        if (isPlayback) {
            for (int i = 0; i < bars.size(); i++) {
                float randomHeight = MIN_BAR_HEIGHT + random.nextInt(MAX_RANDOM - MIN_BAR_HEIGHT);
                bars.get(i).setTargetHeight(randomHeight);
            }
        }

        boolean stillAnimating = false;
        for (Bar bar : bars) {
            stillAnimating |= bar.animate();
        }

        if (!stillAnimating && isPlayback) {
            updateBars();
        }
    }

    @Override
    public void startAnimation() {
        isAnimating = true;
        isPlayback = false;
        invalidate();
    }

    @Override
    public void stopAnimation() {
        isAnimating = false;
        isPlayback = false;

        for (Bar bar : bars) {
            bar.setHeight(MIN_BAR_HEIGHT);
            bar.setTargetHeight(MIN_BAR_HEIGHT);
        }

        invalidate();
    }

    @Override
    public void playbackAnimation() {
        isAnimating = false;
        isPlayback = true;
        invalidate();
    }

    private static class Bar {
        private float height;
        private float targetHeight;
        private static final float ANIMATION_SPEED = 0.2f;

        public Bar(float initialHeight) {
            this.height = initialHeight;
            this.targetHeight = initialHeight;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public void setTargetHeight(float targetHeight) {
            this.targetHeight = targetHeight;
        }

        public boolean animate() {
            if (Math.abs(height - targetHeight) < 0.1f) {
                height = targetHeight;
                return false;
            }

            height += (targetHeight - height) * ANIMATION_SPEED;
            return true;
        }
    }
}