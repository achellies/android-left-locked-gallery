package com.scvngr.levelup.views.gallery;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * A scroller that will happily go on infinitely, when friction is set to 0. The physics model is
 * highly simplified compared to other scrollers.
 * 
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 * 
 */
public class InfiniteScroller {

    private static final int SCROLL_MODE = 0;
    private static final int FLING_MODE = 1;

    private long mStartTime;
    private int mVelocity;
    private float mDeceleration;
    private int mCurrentPosition;
    private int mStart;
    private float mPpi;
    private boolean mIsFinished;
    private float mDuration;
    private int mMode;
    private int mDeltaX;
    private Interpolator mInterpolator;
    private float mDurationReciprocal;

    static {
        // This controls the viscous fluid effect (how much of it)
        sViscousFluidScale = 8.0f;
        // must be set to 1.0 (used in viscousFluid())
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }

    private static float sViscousFluidScale;
    private static float sViscousFluidNormalize;

    public InfiniteScroller(Context context) {
        this(context, null);
    }

    public InfiniteScroller(Context context, Interpolator interpolator) {
        mInterpolator = interpolator;
        mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
        mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
    }

    /**
     * Sets the friction to decelerate the scroll on a
     * {@link #fling(int, int, int, int, int, int, int, int)}.
     * 
     * @param friction
     *            should be between 0.0 and 1 or so.
     */
    public final void setFriction(float friction) {
        mDeceleration = computeDeceleration(friction);
    }

    private float computeDeceleration(float friction) {
        return SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * mPpi // pixels per inch
                * friction;
    }

    /**
     * Returns the time elapsed since the beginning of the scrolling.
     * 
     * @return The elapsed time in milliseconds.
     */
    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - mStartTime);
    }

    /**
     * Returns the current velocity.
     * 
     * @return The original velocity less the deceleration. Result may be negative.
     */
    public float getCurrVelocity() {
        return mVelocity + mDeceleration * timePassed() / 2000.0f;
    }

    /**
     * @return the current scroll position
     */
    public int getCurrX() {
        return mCurrentPosition;
    }

    /**
     * @return the computed length of the scroll. If the scroll is infinite (friction = 0), this
     *         will return {@link Float#POSITIVE_INFINITY}.
     */
    public float getDuration() {
        return mDuration;
    }

    public void startScroll(int startX, int startY, int dX, int dY, int duration) {
        mMode = SCROLL_MODE;
        mDuration = duration;
        mStart = startX;
        mDeltaX = dX;
        mDurationReciprocal = 1.0f / (float) mDuration;
        mIsFinished = false;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
    }

    public void forceFinished(boolean finished) {
        mIsFinished = true;
    }

    /**
     * Fling, same interface as {@link Scroller#fling(int, int, int, int, int, int, int, int)}. This
     * only supports the X axis at the moment.
     * 
     * @param startX
     * @param startY
     *            unused
     * @param velocityX
     * @param velocityY
     *            unused
     * @param minX
     * @param maxX
     * @param minY
     *            unused
     * @param maxY
     *            unused
     */
    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX,
            int minY, int maxY) {

        mMode = FLING_MODE;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        mVelocity = velocityX;
        mStart = startX;
        mIsFinished = false;

        if (mDeceleration == 0) {
            mDuration = Float.POSITIVE_INFINITY;
        } else {
            mDuration = -velocityX / (Math.signum(-mVelocity) * mDeceleration) * 1000;
        }
    }

    /**
     * @return true if the scroll has finished.
     */
    public boolean isFinished() {
        return mIsFinished;
    }

    /**
     * Updates the scroll calculation. The result can be retrieved with {@link #getCurrX()}.
     * 
     * @return true if the scroll is still going on
     */
    public boolean computeScrollOffset() {

        final long time = AnimationUtils.currentAnimationTimeMillis();
        final long currentTime = time - mStartTime;

        final float t = currentTime / 1000.0f;

        if (currentTime < mDuration) {
            switch (mMode) {
                case SCROLL_MODE:
                    float x = currentTime * mDurationReciprocal;

                    if (mInterpolator == null)
                        x = viscousFluid(x);
                    else
                        x = mInterpolator.getInterpolation(x);

                    mCurrentPosition = mStart + Math.round(x * mDeltaX);
                    break;

                case FLING_MODE: {
                    float distance = mVelocity * t + Math.signum(-mVelocity) * mDeceleration * t
                            * t / 2.0f;

                    mCurrentPosition = mStart + (int) Math.round(distance);
                    break;

                }

            }
        } else {
            mIsFinished = true;
        }

        return !mIsFinished;
    }

    static float viscousFluid(float x) {
        x *= sViscousFluidScale;
        if (x < 1.0f) {
            x -= (1.0f - (float) Math.exp(-x));
        } else {
            float start = 0.36787944117f; // 1/e == exp(-1)
            x = 1.0f - (float) Math.exp(1.0f - x);
            x = start + x * (1.0f - start);
        }
        x *= sViscousFluidNormalize;
        return x;
    }
}
