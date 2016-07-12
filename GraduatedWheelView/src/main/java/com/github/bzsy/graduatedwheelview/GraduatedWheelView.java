package com.github.bzsy.graduatedwheelview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by bzsy on 15/10/1.
 */

public class GraduatedWheelView extends View {
    public static final int TYPE_DECIMAL = 1;
    public static final int TYPE_INTEGER = 2;

    private int valueType = TYPE_INTEGER;//小数 or 整数
    private int width, height;//控件宽高
    private float minVal = 0f;
    private float maxVal = 100f;//刻度范围
    private float curValue = 0f;//当前选中值
    private float preValue = 0f;//上一个选中值
    private int curMove = 0;//当前刻度位移距离
    private Paint divLinePaint;
    private Paint centerLinePaint;
    private TextPaint textPaint;
    private TextPaint centerTextPaint;

    private float density = getContext().getResources().getDisplayMetrics().density;
    private int textSize = 16;//字号
    private int textColor = getResources().getColor(android.R.color.black);//字体颜色
    private int foreColors[] = {0xdddde4eb, 0x00ffffff, 0xdddde4eb};//前景渐变颜色
    private int strokeColor = getResources().getColor(android.R.color.holo_blue_dark);//外框颜色
    private int strokeWidth = 1;//外框宽度
    private float cornerRadius = 3 * density;//外框边角弧度
    private int centerLineColor = getResources().getColor(android.R.color.holo_blue_dark);//中心刻度线颜色
    private int divLineColor = getResources().getColor(android.R.color.darker_gray);//刻度线颜色
    private int divLineWidth = Math.round(1.5f * density);//刻度线粗细
    private int divGap = Math.round(8 * density);

    private OnValueChangedListener listener;
    private VelocityTracker velocityTracker;
    private Scroller scroller;
    private float minVelocity;
    private int lastPos = 0;
    private boolean hasInit = false;


    public GraduatedWheelView(Context context) {
        this(context, null, 0);
    }

    public GraduatedWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraduatedWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
        this.listener = listener;
    }

    public void setCurValue(float curValue) {
        if (curValue > maxVal) {
            this.curValue = maxVal;
        } else if (curValue < minVal) {
            this.curValue = minVal;
        } else {
            this.curValue = curValue;
        }
        preValue = this.curValue;
        postInvalidate();
    }

    public void setMaxVal(float maxVal) {
        this.maxVal = maxVal;
        setCurValue(curValue);
    }

    public void setMinVal(float minVal) {
        this.minVal = minVal;
        setCurValue(curValue);
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        postInvalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (textPaint == null) {
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        }
        textPaint.setColor(this.textColor);
        textPaint.setTextSize(textSize * density);
        postInvalidate();
    }

    public void setDivLineColor(int divLineColor) {
        this.divLineColor = divLineColor;
        if (divLinePaint == null) {
            divLinePaint = new Paint();
        }
        divLinePaint.setColor(this.divLineColor);
        postInvalidate();
    }

    public void setValueType(int valueType) {
        if (valueType == TYPE_DECIMAL) {
            this.valueType = TYPE_DECIMAL;
        } else {
            this.valueType = TYPE_INTEGER;
        }
        postInvalidate();
    }

    public void setCenterLineColor(int centerLineColor) {
        this.centerLineColor = centerLineColor;
        if (centerLinePaint == null) {
            centerLinePaint = new Paint();
        }
        centerLinePaint.setColor(this.centerLineColor);
        centerLinePaint.setStrokeWidth(divLineWidth);

        if (centerTextPaint == null) {
            centerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        }
        centerTextPaint.setColor(this.centerLineColor);
        centerTextPaint.setTextSize(textSize * density);

        postInvalidate();
    }


    public interface OnValueChangedListener {
        void onChanged(float oldValue, float newValue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDivLine(canvas);
        drawForeground(canvas);
    }

    /**
     * 画方框和小三角
     *
     * @param canvas
     */
    private void drawForeground(Canvas canvas) {
        canvas.save();
        canvas.drawLine(width / 2, 0, width / 2, height / 2, centerLinePaint);

        Drawable stroke = createBackground();
        stroke.setBounds(0, 0, width, height);
        stroke.draw(canvas);
        canvas.restore();
        int padding = (int) (cornerRadius + 1);
        setPadding(padding, padding, padding, padding);//防止弧角外露出刻度线
    }

    private GradientDrawable createBackground() {
        GradientDrawable bgDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, foreColors);
        bgDrawable.setCornerRadius(cornerRadius);
        bgDrawable.setStroke(strokeWidth, strokeColor);
        return bgDrawable;
    }

    private void initParams() {
        width = getWidth();
        height = getHeight();

        if (divLinePaint == null) {
            divLinePaint = new Paint();
        }
        divLinePaint.setStrokeWidth(divLineWidth);
        divLinePaint.setColor(divLineColor);

        velocityTracker = VelocityTracker.obtain();
        scroller = new Scroller(getContext());
        minVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        if (textPaint == null) {
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        }
        textPaint.setTextSize(textSize * density);
        textPaint.setColor(textColor);

        if (centerLinePaint == null) {
            centerLinePaint = new Paint();
        }
        centerLinePaint.setColor(centerLineColor);
        centerLinePaint.setStrokeWidth(divLineWidth);

        if (centerTextPaint == null) {
            centerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        }
        centerTextPaint.setColor(this.centerLineColor);
        centerTextPaint.setTextSize(textSize * density);
    }

    /**
     * 画刻度线
     *
     * @param canvas
     */
    private void drawDivLine(Canvas canvas) {
        canvas.save();
        int center = width / 2 - curMove;
        int drawWidth = 0;
        float value;
        for (int i = 0; drawWidth <= 5 * width; i++) {
            value = valueType == TYPE_INTEGER ? Math.round(preValue) + i : (float) (Math.round(preValue * 10) + i) / 10;
            int divLineLength = getDivLineLength(value);
            canvas.drawLine(center + i * divGap, getPaddingTop(), center + i * divGap, divLineLength, divLinePaint);
            if (value <= maxVal) {
                drawValue(canvas, value, center + i * divGap);
            }

            value = valueType == TYPE_INTEGER ? Math.round(preValue) - i : (float) (Math.round(preValue * 10) - i) / 10;
            divLineLength = getDivLineLength(value);
            canvas.drawLine(center - i * divGap, getPaddingTop(), center - i * divGap, divLineLength, divLinePaint);
            if (value >= minVal) {
                drawValue(canvas, value, center - i * divGap);
            }
            drawWidth += 2 * divGap;
        }
        canvas.restore();
    }

    /**
     * 画刻度值文字
     *
     * @param canvas
     * @param value
     * @param posX
     */
    private void drawValue(Canvas canvas, float value, int posX) {
        if (valueType == TYPE_DECIMAL && value != (int) value) {
            return;
        }
        if (valueType == TYPE_INTEGER && value % 10 != 0) {
            return;
        }
        String valueString = valueType == TYPE_INTEGER ? String.valueOf((int) value) : String.format("%.1f", value);
        float textWidth = Layout.getDesiredWidth("0", textPaint);
        if (value == curValue) {
            canvas.drawText(valueString, posX - textWidth * valueString.length() / 2, height / 2 + 2 * textWidth, centerTextPaint);
        } else {
            canvas.drawText(valueString, posX - textWidth * valueString.length() / 2, height / 2 + 2 * textWidth, textPaint);
        }
    }

    /**
     * 计算刻度线长度
     *
     * @param value
     * @return
     */
    private int getDivLineLength(float value) {
        int divLineLength;
        if (valueType == TYPE_INTEGER) {
            if (value % 10 == 0) {
                divLineLength = height / 2;
            } else if (value % 5 == 0) {
                divLineLength = height / 3;
            } else {
                divLineLength = height / 4;
            }
        } else {
            if (value - (int) value == 0f) {
                divLineLength = height / 2;
            } else if (value - (int) value == 0.5f) {
                divLineLength = height / 3;
            } else {
                divLineLength = height / 4;
            }
        }
        return divLineLength;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!hasInit) {
            initParams();
            hasInit = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        int curPos = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetScroll();
                lastPos = curPos;
                break;
            case MotionEvent.ACTION_MOVE:
                if (curValue <= minVal && curPos > lastPos) {
                    curMove += (lastPos - curPos) / 20;
                } else if (curValue >= maxVal && curPos < lastPos) {
                    curMove += (lastPos - curPos) / 20;
                } else {
                    curMove += lastPos - curPos;
                }
                lastPos = curPos;
                countValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countValue();
                preValue = curValue;
                curMove = 0;
                if (curValue > minVal && curValue < maxVal) {
                    countVelocityTracker(500);
                }
        }
        postInvalidate();
        return true;
    }

    /**
     * 惯性继续滚动
     *
     * @param units
     */
    private void countVelocityTracker(int units) {
        velocityTracker.computeCurrentVelocity(units);
        float xVelocity = velocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > minVelocity) {
            scroller.fling(lastPos, 0, (int) xVelocity, 0, lastPos - width, lastPos + width, 0, 0);
        }
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            if (scroller.getCurrX() == scroller.getFinalX()) {
                countValue();
                preValue = curValue;
                curMove = 0;
            } else {
                int curPos = scroller.getCurrX();
                if (curValue <= minVal && curPos > lastPos) {
                    resetScroll();
                } else if (curValue >= maxVal && curPos < lastPos) {
                    resetScroll();
                } else {
                    curMove += lastPos - curPos;
                }
                lastPos = curPos;
                countValue();
            }
            postInvalidate();
        }
    }

    /**
     * 重置滚动
     */
    private void resetScroll() {
        scroller.forceFinished(true);
        preValue = curValue;
        curMove = 0;
    }

    /**
     * 计算当前选中的值
     */
    private void countValue() {
        int move = Math.round(curMove / divGap);
        curValue = valueType == TYPE_INTEGER ? preValue + move : (float) (Math.round(preValue * 10) + move) / 10;
        curValue = curValue <= minVal ? minVal : curValue;
        curValue = curValue > maxVal ? maxVal : curValue;
        if (listener != null) {
            listener.onChanged(preValue, curValue);
        }
    }
}