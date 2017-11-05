package com.cxmax.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.cxmax.widget.utils.AnimUtil;
import com.cxmax.widget.utils.DevicesUtil;

/**
 * @describe : animator realized in {@link ILikeState}
 * @usage :
 * <p>
 * </p>
 * Created by caixi on 17-10-28.
 */

public class LikeView extends View implements ILikeView{

    private static final String TAG = "widget.LikeView";
    public static final String ARGS = "agrs";
    public static final String STATE = "state";

    @DrawableRes private static final int RES_MIPMAP_LIKE;
    @DrawableRes private static final int RES_MIPMAP_DISLIKE;
    @DrawableRes private static final int RES_MIPMAP_SHINING;

    private static final int DIMEN_SHINNING_BITMAP_START_PADDING;
    private static final int DIMEN_LIKE_BITMAP_TOP_PADDING;
    /* animator param */
    private static final int SCALE_DURING = 150;
    private static final float SCALE_MIN = 0.9f;
    private static final float SCALE_MAX = 1f;

    @NonNull private LikeState state;

    /* draw bitmap params */
    private Bitmap likeBitmap;
    private Bitmap dislikeBitmap;
    private Bitmap shiningBitmap;
    private Paint bitmapPaint;

    private float likeWidth;
    private float likeHeight;
    private float shiningWidth;
    private float shiningHeight;

    private int defaultWidth;
    private int defaultHeight;

    @NonNull private Point likePoint;
    @NonNull private Point shiningPoint;

    private int clickCount;
    private int lastClickCount;
    private long lastAnimStartTime;

    enum LikeState {
        LIKE, DISLIKE
    }

    static {
        RES_MIPMAP_LIKE = R.mipmap.ic_messages_like_selected;
        RES_MIPMAP_DISLIKE = R.mipmap.ic_messages_like_unselected;
        RES_MIPMAP_SHINING = R.mipmap.ic_messages_like_selected_shining;
        DIMEN_SHINNING_BITMAP_START_PADDING = 2;
        DIMEN_LIKE_BITMAP_TOP_PADDING = 8;
    }

    {
        likePoint = new Point();
        shiningPoint = new Point();
        state = LikeState.DISLIKE;
    }

    public LikeView(Context context) {
        this(context, null);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeCustomAttrs(context, attrs);
        initialize(context);
    }

    private void initializeCustomAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LikeView, 0, 0);
            if (attr != null) {
                state = attr.getBoolean(R.styleable.LikeView_like_state, false) ? LikeState.LIKE : LikeState.DISLIKE;
                attr.recycle();
            }
        }
    }

    private void initialize(Context context) {
        likeBitmap = BitmapFactory.decodeResource(context.getResources(), RES_MIPMAP_LIKE);
        dislikeBitmap = BitmapFactory.decodeResource(context.getResources(), RES_MIPMAP_DISLIKE);
        shiningBitmap = BitmapFactory.decodeResource(context.getResources(), RES_MIPMAP_SHINING);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        likeWidth = likeBitmap.getWidth();
        likeHeight = likeBitmap.getHeight();
        shiningWidth = shiningBitmap.getWidth();
        shiningHeight = shiningBitmap.getHeight();

        likePoint.x = getPaddingStart();
        likePoint.y = getPaddingTop() + DevicesUtil.dp2px(context, DIMEN_LIKE_BITMAP_TOP_PADDING);

        shiningPoint.x = getPaddingStart() + DevicesUtil.dp2px(context, DIMEN_SHINNING_BITMAP_START_PADDING);
        shiningPoint.y = getPaddingTop();

        defaultWidth = likeBitmap.getWidth() > shiningBitmap.getWidth() ?
                likeBitmap.getWidth() : shiningBitmap.getWidth();
        defaultHeight = likeBitmap.getHeight() + shiningBitmap.getHeight() + getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureSize(widthMeasureSpec, defaultWidth);
        int height = measureSize(heightMeasureSpec, defaultHeight);
        if (width < height) {
            setMeasuredDimension(height, height);
        } else {
            setMeasuredDimension(width, width);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        LikeStateFactory.of(state)
                .drawBitmap(canvas, likeBitmap, dislikeBitmap, shiningBitmap, likePoint, shiningPoint, bitmapPaint);
        canvas.restore();
    }

    private int measureSize(int measureSpec, int defaultSize) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;
            result += getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle args = new Bundle();
        args.putParcelable(ARGS, super.onSaveInstanceState());
        args.putBoolean(STATE, state == LikeState.LIKE);
        return args;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle args = (Bundle) state;
            Parcelable data = args.getParcelable(ARGS);
            super.onRestoreInstanceState(data);
            this.state = args.getBoolean(STATE, false) ? LikeState.LIKE : LikeState.DISLIKE;
            initialize(getContext());
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    @Override
    public void startAnim() {
        LikeStateFactory.of(state)
                .startAnim(this, isQuickTap());
        lastClickCount = clickCount;
    }

    @Override
    public void release() {
        /* release resource */
        LikeStateFactory.of(state)
                .release();
    }

    public void setLikeState(LikeState state) {
        this.state = state;
        clickCount = state == LikeState.LIKE ? 1 : 0;
        lastClickCount = clickCount;

        postInvalidate();
    }

    private void setDislikeScale(float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        dislikeBitmap = BitmapFactory.decodeResource(getResources(), RES_MIPMAP_DISLIKE);
        dislikeBitmap = Bitmap.createBitmap(dislikeBitmap, 0, 0, dislikeBitmap.getWidth(), dislikeBitmap.getHeight(),
                matrix, true);

        postInvalidate();
    }

    private void setLikeScale(float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        likeBitmap = BitmapFactory.decodeResource(getResources(), RES_MIPMAP_LIKE);
        likeBitmap = Bitmap.createBitmap(likeBitmap, 0, 0, likeBitmap.getWidth(), likeBitmap.getHeight(),
                matrix, true);

        postInvalidate();
    }

    private void setShiningScale(float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        shiningBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected_shining);
        shiningBitmap = Bitmap.createBitmap(shiningBitmap, 0, 0, shiningBitmap.getWidth(), shiningBitmap.getHeight(),
                matrix, true);

        postInvalidate();
    }

    private boolean isQuickTap() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastAnimStartTime < 300) {
            return true;
        }
        lastAnimStartTime = currentTimeMillis;
        return false;
    }

    private static class LikeStateFactory {

        static ILikeState of(LikeState state) {
            if (state == LikeState.LIKE) {
                return LikeStateStrategy.getInstance();
            }
            return DislikeStateStrategy.getInstance();
        }
    }

    private static class LikeStateStrategy implements ILikeState {

        private ObjectAnimator likeScale;
        private ObjectAnimator quickTapLickScale;

        private static class SingletonHolder {
            public final static LikeStateStrategy instance = new LikeStateStrategy();
        }

        public static LikeStateStrategy getInstance() {
            return SingletonHolder.instance;
        }

        @Override
        public void drawBitmap(Canvas canvas, Bitmap like, Bitmap dislike, Bitmap shining, Point likePoint, Point shiningPoint, Paint paint) {
            canvas.drawBitmap(shining, shiningPoint.x, shiningPoint.y, paint);
            canvas.drawBitmap(like, likePoint.x, likePoint.y, paint);
        }

        @Override
        public void startAnim(LikeView likeView, boolean isQuickTap) {
            if (isQuickTap) {
                playQuickTapAnim(likeView);
            } else {
                playNormalAnim(likeView);
            }
            likeView.clickCount = 0;
        }

        @Override
        public void release() {
            AnimUtil.remove(likeScale, quickTapLickScale);
        }

        private void playNormalAnim(final LikeView likeView) {
            likeScale = ObjectAnimator.ofFloat(likeView, "likeScale", SCALE_MIN, SCALE_MAX);
            likeScale.setDuration(SCALE_DURING);
            likeScale.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    likeView.state = LikeState.DISLIKE;
                    likeView.setDislikeScale(SCALE_MAX);
                }
            });
            likeScale.start();
        }

        private void playQuickTapAnim(final LikeView likeView) {
            quickTapLickScale = ObjectAnimator.ofFloat(likeView, "likeScale", SCALE_MIN, SCALE_MAX);
            quickTapLickScale.setDuration(SCALE_DURING);
            quickTapLickScale.setInterpolator(new OvershootInterpolator());
            quickTapLickScale.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    likeView.lastClickCount++;
                    if (likeView.clickCount != likeView.lastClickCount) {
                        return;
                    }
                    if (likeView.clickCount % 2 == 0) {
                        playNormalAnim(likeView);
                    }
                }
            });
            quickTapLickScale.start();
        }
    }

    private static class DislikeStateStrategy implements ILikeState {

        private ObjectAnimator dislikeScale;
        private ObjectAnimator likeScale;
        private ObjectAnimator shiningScale;
        private AnimatorSet animatorSet;

        private static class SingletonHolder {
            public final static DislikeStateStrategy instance = new DislikeStateStrategy();
        }

        public static DislikeStateStrategy getInstance() {
            return SingletonHolder.instance;
        }

        @Override
        public void drawBitmap(Canvas canvas, Bitmap like, Bitmap dislike, Bitmap shining, Point likePoint, Point shiningPoint, Paint paint) {
            canvas.drawBitmap(dislike, likePoint.x, likePoint.y, paint);
        }

        @Override
        public void startAnim(final LikeView likeView, boolean isQuickTap) {
           if (animatorSet != null) {
               likeView.clickCount = 0;
           } else {
               playNormalAnim(likeView);
               likeView.clickCount = 1;
           }

        }

        @Override
        public void release() {
            AnimUtil.remove(dislikeScale, likeScale, shiningScale, animatorSet);
        }

        private void playNormalAnim(final LikeView likeView) {
            dislikeScale = ObjectAnimator.ofFloat(likeView, "dislikeScale", SCALE_MAX, SCALE_MIN);
            dislikeScale.setDuration(SCALE_DURING);
            dislikeScale.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    likeView.state = LikeState.LIKE;
                }
            });

            likeScale = ObjectAnimator.ofFloat(likeView, "likeScale", SCALE_MIN, SCALE_MAX);
            likeScale.setDuration(SCALE_DURING);
            likeScale.setInterpolator(new OvershootInterpolator());

            shiningScale = ObjectAnimator.ofFloat(likeView, "shiningScale", SCALE_MIN, SCALE_MAX);
            shiningScale.setDuration(SCALE_DURING);
            shiningScale.setInterpolator(new OvershootInterpolator());

            animatorSet = new AnimatorSet();
            animatorSet.play(likeScale).with(shiningScale).after(dislikeScale);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animatorSet = null;
                }
            });
            animatorSet.start();
        }
    }

    private interface ILikeState {
        void drawBitmap(Canvas canvas, Bitmap like, Bitmap dislike, Bitmap shining, Point likePoint, Point shiningPoint, Paint paint);

        void startAnim(LikeView likeView, boolean isQuickTap);

        void release();
    }
}
