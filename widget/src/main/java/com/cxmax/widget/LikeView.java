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

    @DrawableRes private static final int RES_LIKE_SELECTED;
    @DrawableRes private static final int RES_LIKE_UNSELECTED;
    @DrawableRes private static final int RES_LIKE_SELECTED_SHINING;

    private Context context;

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
        initializeCommonParams(context);
    }

    private void initializeCommonParams(Context context) {
        likeSelectedBitmap = BitmapFactory.decodeResource(context.getResources(), RES_LIKE_SELECTED);
        likeUnselectedBitmap = BitmapFactory.decodeResource(context.getResources(), RES_LIKE_UNSELECTED);
        likeSelectedShiningBitmap = BitmapFactory.decodeResource(context.getResources(), RES_LIKE_SELECTED_SHINING);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public static int measureSize(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
            case View.MeasureSpec.AT_MOST:
                break;
            case View.MeasureSpec.EXACTLY:
                result = specSize;
                result = Math.max(result, defaultSize);
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
