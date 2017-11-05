package com.cxmax.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.cxmax.widget.LikeView.*;
import com.cxmax.widget.utils.DevicesUtil;

/**
 * @describe :
 * @usage :
 * <p>
 * </p>
 * Created by caixi on 17-11-5.
 */

public class LikeNumberView extends LinearLayout implements View.OnClickListener{

    private static final String TAG = "widget.LikeNumberView";
    private static final int DIMEN_DRAWABLE_PADDING;

    /* custom params */
    @NonNull private LikeState state;
    @NonNull private boolean like;
    private int count;
    @ColorInt private int textColor;
    private int textSize;
    private int drawablePadding;

    private NumberView numberView;
    private LikeView likeView;

    static {
        DIMEN_DRAWABLE_PADDING = 4;
    }

    {
        state = LikeState.DISLIKE;
        like = Boolean.FALSE;
    }

    public LikeNumberView(Context context) {
        this(context, null);
    }

    public LikeNumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeCustomAttrs(context, attrs);
        initialize(context);
    }

    private void initializeCustomAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.LikeNumberView, 0, 0);
            if (attr != null) {
                like = attr.getBoolean(R.styleable.LikeNumberView_like_number_state, false);
                state = like ? LikeState.LIKE : LikeState.DISLIKE;
                count = attr.getInteger(R.styleable.LikeNumberView_like_number_count, 0);
                textColor = attr.getColor(R.styleable.LikeNumberView_like_number_text_color, ContextCompat.getColor(context, R.color.black));
                textSize = attr.getDimensionPixelSize(R.styleable.LikeNumberView_like_number_text_size, context.getResources().getDimensionPixelSize(R.dimen.common_text_size_12));
                drawablePadding = attr.getDimensionPixelSize(R.styleable.LikeNumberView_like_number_drawable_padding, DevicesUtil.dp2px(context, DIMEN_DRAWABLE_PADDING));
                attr.recycle();
            }
        }
    }

    private void initialize(Context context) {
        removeAllViews();
        setClipChildren(false);
        setOrientation(LinearLayout.HORIZONTAL);

        addLikeView(context);
        addNumberView(context);
        setOnClickListener(this);
    }

    private void addLikeView(Context context) {
        likeView = new LikeView(context);
        likeView.setLikeState(state);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(likeView, params);
    }

    private void addNumberView(Context context) {
        numberView = new NumberView(context);
        numberView.setTextColor(textColor);
        numberView.setTextSize(textSize);
        numberView.setCount(count);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = drawablePadding;
        params.topMargin += getPaddingTop();
        params.bottomMargin = getPaddingBottom();
        params.rightMargin = getPaddingRight();
        addView(numberView, params);
    }

    @Override
    public void onClick(View view) {
        like = !like;
        if (like) {
            numberView.increase();
        } else {
            numberView.decrease();
        }
        likeView.startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (likeView != null) {
            likeView.release();
        }
        if (numberView != null) {
            numberView.release();
        }
    }
}
