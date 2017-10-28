package com.cxmax.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.cxmax.widget.utils.DevicesUtil;

/**
 * @describe :
 * @usage :
 * <p>
 * </p>
 * Created by caixi on 17-10-28.
 */

public class LikeView extends View {

    private static final String TAG = "com.cxmax.widget.LikeView";

    @DrawableRes
    private static final int RES_LIKE_SELECTED;
    private static final int RES_LIKE_UNSELECTED;
    private static final int RES_LIKE_SELECTED_SHINING;
    private static final String DEFAULT_LIKE_COUNT;

    private Context context;
    /* attr params */
    private int likeTextSize;
    private int likeDrawablePadding;
    private String likeCount;
    @ColorInt
    private int likeTextColor;

    /* basic params */
    private Bitmap likeSelectedBitmap;
    private Bitmap likeUnselectedBitmap;
    private Bitmap likeSelectedShiningBitmap;
    private int width, height;
    private Paint paint;

    static {
        RES_LIKE_SELECTED = R.mipmap.ic_messages_like_selected;
        RES_LIKE_UNSELECTED = R.mipmap.ic_messages_like_unselected;
        RES_LIKE_SELECTED_SHINING = R.mipmap.ic_messages_like_selected_shining;
        DEFAULT_LIKE_COUNT = "12";
    }

    public LikeView(Context context) {
        this(context, null);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initializeCustomAttrs(context, attrs);
        initializeCommonParams(context);
    }

    private void initializeCommonParams(Context context) {
        likeSelectedBitmap = BitmapFactory.decodeResource(context.getResources(), RES_LIKE_SELECTED);
        likeUnselectedBitmap = BitmapFactory.decodeResource(context.getResources(), RES_LIKE_UNSELECTED);
        likeSelectedShiningBitmap = BitmapFactory.decodeResource(context.getResources(), RES_LIKE_SELECTED_SHINING);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initializeCustomAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LikeView, 0, 0);
            if (attr != null) {
                likeDrawablePadding = attr.getDimensionPixelSize(R.styleable.LikeView_like_drawable_padding, context.getResources().getDimensionPixelSize(R.dimen.common_text_size_12));
                likeTextSize = attr.getDimensionPixelSize(R.styleable.LikeView_like_text_size, context.getResources().getDimensionPixelSize(R.dimen.common_text_size_12));
                likeTextColor = attr.getColor(R.styleable.LikeView_like_text_size, ContextCompat.getColor(context, R.color.black));
                likeCount = attr.getString(R.styleable.LikeView_like_count);
                attr.recycle();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            width = DevicesUtil.getDisplayMetrics(context).widthPixels;
        }

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            height = DevicesUtil.getDisplayMetrics(context).heightPixels;
        }

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawableInitialState(canvas);
    }

    private void drawableInitialState(Canvas canvas) {
        canvas.save();
        canvas.drawBitmap(likeUnselectedBitmap, 0 , height / 2, paint);
        canvas.restore();
    }
}
