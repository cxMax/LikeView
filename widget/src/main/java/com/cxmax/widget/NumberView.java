package com.cxmax.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.cxmax.widget.utils.AnimUtil;
import com.cxmax.widget.utils.ComputeUtil;
import com.cxmax.widget.utils.DevicesUtil;

/**
 * @describe :
 * @usage :
 * <p>
 * </p>
 * Created by caixi on 17-11-4.
 */

public class NumberView extends View implements INumberView {

    private static final String TAG = "widget.NumberView";
    private static final int DEFAULT_ANIMATOR_DURATION = 150;

    private Context context;
    private Paint paint;

    private String[] nums;
    /* 执行动画的相关参数 */
    private ObjectAnimator changeAnimator;
    private float textYFlag; // 执行动画的Y轴偏移量 总是从 0.0f 到 1.0f
    private float originChangeY;
    private float currentChangeY;
    @ColorInt
    private int originColor;
    @ColorInt
    private int currentColor;
    private ChangeOffsetState state; // 增加 和 减少 两种偏移量计算

    /* custom params */
    private int count;
    @ColorInt
    private int textColor;
    private int textSize;

    {
        nums = new String[3];
        textYFlag = 0.0f;
        originChangeY = 0.0f;
        currentChangeY = 0.0f;
    }

    private enum ChangeOffsetState {
        INCREASE,
        DECREASE
    }

    public NumberView(Context context) {
        this(context, null);
    }

    public NumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initializeCustomAttrs(context, attrs);
        initializeCommonParams(context);
        initializeObjectAnimator();
        computeNum(0);
    }

    @Override
    public void increase() {
        changeCount(count + 1);
    }

    @Override
    public void decrease() {
        changeCount(count - 1);
    }

    @Override
    public void release() {
        AnimUtil.remove(changeAnimator);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureSize(widthMeasureSpec, measureContentWidth() + getPaddingStart() + getPaddingEnd()),
                measureSize(heightMeasureSpec, measureContentHeight() + getPaddingStart() + getPaddingEnd()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // set draw text baseline
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        String number = String.valueOf(count);

        // draw number in turn
        float startX = 0;
        float baseY = getHeight() / 2 - fontMetrics.top / 2 - fontMetrics.bottom / 2;

        // first draw the common block between origin and current number;
        paint.setColor(textColor);
        canvas.drawText(nums[0], startX, baseY, paint);

        float charWidth = paint.measureText(number) / number.length();

        // second draw the changeable back block of original number
        paint.setColor(originColor);
        canvas.drawText(nums[1], startX + charWidth * nums[0].length(), baseY + originChangeY, paint);

        // third draw the changeable back block of current number
        paint.setColor(currentColor);
        canvas.drawText(nums[2], startX + charWidth * nums[0].length(), baseY + currentChangeY, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    private void initializeCustomAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.NumberView, 0, 0);
            if (attr != null) {
                count = attr.getInteger(R.styleable.NumberView_number_count, 0);
                textColor = attr.getColor(R.styleable.NumberView_number_text_color, ContextCompat.getColor(context, R.color.black));
                textSize = attr.getDimensionPixelSize(R.styleable.NumberView_number_text_size, context.getResources().getDimensionPixelSize(R.dimen.common_text_size_12));
                attr.recycle();
            }
        }
    }

    private void initializeCommonParams(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
    }

    private void initializeObjectAnimator() {
        changeAnimator = ObjectAnimator.ofFloat(this, "textYFlag", 0.0f, 1.0f);
        changeAnimator.setDuration(DEFAULT_ANIMATOR_DURATION);
    }

    private static int measureSize(int measureSpec, int defaultSize) {
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

    private int measureContentWidth() {
        return (int) paint.measureText(String.valueOf(count));
    }

    private int measureContentHeight() {
        return DevicesUtil.sp2px(context, textSize);
    }

    /**
     * 把整个字符串 拆分成 共有不变的前部 / (原来数字)变化的后部分 / (当前数字)变化的后部分, 然后分别放进数组中 , 做转换动画
     * <p>
     * reference : https://insight.io/github.com/arvinljw/ThumbUpSample/tree/master
     *
     * @param change 增长量
     */
    private void computeNum(int change) {
        if (change == 0) {
            nums[0] = String.valueOf(count);
            nums[1] = "";
            nums[2] = "";
            return;
        }

        state = change > 0 ? ChangeOffsetState.INCREASE : ChangeOffsetState.DECREASE;

        String origin = String.valueOf(count);
        String current = String.valueOf(count + change);

        if (origin.length() != current.length()) {
            nums[0] = "";
            nums[1] = origin;
            nums[2] = current;
        } else {
            for (int i = 0; i < origin.length(); i++) {
                char oChar = origin.charAt(i);
                char cChar = current.charAt(i);
                if (oChar != cChar) {
                    if (i == 0) {
                        nums[0] = "";
                    } else {
                        nums[0] = current.substring(0, i);
                    }
                    nums[1] = origin.substring(i);
                    nums[2] = current.substring(i);
                    break;
                }
            }
        }
        count += change;
    }

    private void formatColor() {
        originColor = (int) ComputeUtil.evaluate(textYFlag, textColor, 0x00000000);
        currentColor = (int) ComputeUtil.evaluate(textYFlag, 0x00000000, textColor);
    }

    private void changeCount(int number) {
        if (number >= 0) {
            computeNum(number - count);
            changeAnimator.start();
        }
    }

    public float getTextYFlag() {
        return textYFlag;
    }

    public void setTextYFlag(float textYFlag) {
        this.textYFlag = textYFlag;

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.bottom / 2 - fontMetrics.top / 2;
        if (state == ChangeOffsetState.INCREASE) {
            originChangeY = -(textHeight * textYFlag);
            currentChangeY = textHeight * (1 - textYFlag);
        } else {
            currentChangeY = -(textHeight * (1 - textYFlag));
            originChangeY = textHeight * textYFlag;
        }
        formatColor();
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setCount(int count) {
        this.count = count;
        computeNum(0);
        if (changeAnimator != null && count > 0) {
            changeAnimator.start();
        }
        requestLayout();
    }
}
