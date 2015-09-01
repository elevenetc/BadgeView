package su.levenetc.android.badgeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.LinkedList;

import su.levenetc.android.badgeview.interfaces.IAnimationListener;
import su.levenetc.android.badgeview.utils.StringUtils;
import su.levenetc.android.badgeview.values.BitmapValue;
import su.levenetc.android.badgeview.values.IValue;
import su.levenetc.android.badgeview.values.TextValue;

/**
 * Created by Eugene Levenetc.
 */
public class BadgeView extends AbstractBadgeView {

    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final float DEFAULT_TEXT_SIZE = 18;

    private Paint textPaint = new Paint();

    public BadgeView(Context context) {
        super(context);
        config();
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        config();
        handleAttributes(context, attrs);
    }

    private void config() {
        configTextPaint();
        textPaint.setTextSize(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE,
                        getResources().getDisplayMetrics()
                )
        );
        textPaint.setColor(DEFAULT_TEXT_COLOR);

        valueCenter = new TextValue(" ", textPaint);
        valueTop = new TextValue(" ", textPaint);
        valueBottom = new TextValue(" ", textPaint);
    }

    public void setTextSize(float textSize) {
        textPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, textSize,
                getResources().getDisplayMetrics()
        ));
        if (valueCenter instanceof TextValue) {
            valueCenter.calculateBounds();
            requestLayout();
        }
    }


    private void handleAttributes(Context context, AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeView);
        CharSequence text = array.getText(R.styleable.BadgeView_badgeText);
        textPaint.setTextSize(array.getDimension(R.styleable.BadgeView_badgeTextSize, DEFAULT_TEXT_SIZE));
        textPaint.setColor(array.getColor(R.styleable.BadgeView_badgeTextColor, DEFAULT_TEXT_COLOR));
        int bitmapResource = array.getResourceId(R.styleable.BadgeView_badgeBitmap, -1);
        array.recycle();

        if (text != null && bitmapResource != -1) {
            throw new IllegalArgumentException("Trying to pass badgeText and badgeBitmap attrs simultaneously.");
        } else if (text != null) {
            valueCenter = new TextValue(text, textPaint);
        } else if (bitmapResource != -1) {
            valueCenter = new BitmapValue(BitmapFactory.decodeResource(context.getResources(), bitmapResource));
        }
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
        configTextPaint();
        if (valueCenter instanceof TextValue) {
            ((TextValue) valueCenter).setPaint(textPaint);
            if (getVisibility() == VISIBLE) requestLayout();
        }
    }

    private void configTextPaint() {
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setTextColor(int textColor) {

        if (valueCenter instanceof TextValue) {
            ((TextValue) valueCenter).setTextColor(textColor);
            invalidate();
        }
    }

    /**
     * If current value does not represent Bitmap then returns null
     */
    public
    @Nullable
    Bitmap getBitmap() {
        if (valueCenter instanceof BitmapValue) {
            return ((BitmapValue) valueCenter).getValue();
        } else {
            return null;
        }
    }

    /**
     * If current value does not represent number(String or Bitmap) then returns 0
     */
    public double getNumberValue() {
        if (StringUtils.isNumber(valueCenter)) {
            return ((TextValue) valueCenter).getNumber();
        } else {
            return 0;
        }
    }

    public void setValues(Object... values) {
        LinkedList<ValueAnimation> list = new LinkedList<>();
        for (Object value : values) {

            if (value instanceof Number) {
                list.add(new ValueAnimation(new TextValue((Number) value, textPaint), animationDuration));
            } else if (value instanceof CharSequence) {
                list.add(new ValueAnimation(new TextValue((CharSequence) value, textPaint), animationDuration));
            } else if (value instanceof Bitmap) {
                list.add(new ValueAnimation(new BitmapValue((Bitmap) value), animationDuration));
            }

        }
        setValues(list);
    }

    private static IValue toValue(Object value, Paint textPaint) {
        if (value instanceof Number) {
            return new TextValue((Number) value, textPaint);
        } else if (value instanceof CharSequence) {
            return new TextValue((CharSequence) value, textPaint);
        } else if (value instanceof Bitmap) {
            return new BitmapValue((Bitmap) value);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + (value != null ? value.getClass().getSimpleName() : null));
        }
    }

    public void setValue(int value) {
        setValue(value, true);
    }

    public void setValue(int value, boolean animate) {
        setValue(new TextValue(value, textPaint), animate);
    }

    public void setValue(float value) {
        super.setValue(new TextValue(value, textPaint), true);
    }

    public void setValue(@NonNull String value, boolean animate) {
        if (value.isEmpty()) return;
        super.setValue(new TextValue(value, textPaint), animate);
    }

    public void setValue(@NonNull String value) {
        setValue(value, true);
    }

    public void setValue(@NonNull Bitmap bitmap) {
        setValue(bitmap, true);
    }

    public void setValue(@NonNull Bitmap bitmap, boolean animate) {
        super.setValue(new BitmapValue(bitmap), animate);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parc) {
        super.onRestoreInstanceState(parc);
        if (valueCenter instanceof TextValue) {
            textPaint = ((TextValue) valueCenter).getPaint();
        }
    }

    /**
     * Helper animation class which combines several values with different durations.
     */
    public static class AnimationSet {

        LinkedList<ValueAnimation> list = new LinkedList<>();
        private BadgeView badgeView;

        public AnimationSet(BadgeView badgeView) {
            this.badgeView = badgeView;
        }

        public AnimationSet addDelay(long duration) {
            list.add(new Delay(duration));
            return this;
        }

        public AnimationSet add(Object value, long duration) {
            list.add(new ValueAnimation(toValue(value, badgeView.textPaint), duration));
            return this;
        }

        public void play() {
            badgeView.setValues(list);
        }

        public void play(IAnimationListener listener) {
            badgeView.setAnimationListener(listener);
            badgeView.setValues(list);
        }
    }
}
