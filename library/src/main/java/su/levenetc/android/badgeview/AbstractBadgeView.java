package su.levenetc.android.badgeview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

import su.levenetc.android.badgeview.animations.ChangeLayoutSizeAnimation;
import su.levenetc.android.badgeview.interfaces.IAnimationListener;
import su.levenetc.android.badgeview.utils.AbstractAnimatorListener;
import su.levenetc.android.badgeview.utils.ViewUtils;
import su.levenetc.android.badgeview.values.IValue;

/**
 * Basic class which is aimed to animate values which implements {@link IValue}
 * To add functionality which is not related to animation {@link BadgeView}
 */
class AbstractBadgeView extends View {

	private static final String TAG = AbstractBadgeView.class.getSimpleName();

	protected Background background = new Background();
	protected IValue<?> valueTop;
	protected IValue<?> valueCenter;
	protected IValue<?> valueBottom;
	protected IValue<?> bufferValue;

	private boolean isHidden;
	private boolean isAnimating;
	private float animYShift;
	private float finalHeight;

	private int padding = 10;//dp
	private LinkedList<ValueAnimation> animations;
	private FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();
	protected int animationDuration = 250;//ms
	private int backgroundColor = Color.RED;
	private IAnimationListener animationListener;

	public AbstractBadgeView(Context context) {
		super(context);
		config();
	}

	public AbstractBadgeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		handleAttributes(context, attrs);
		config();
	}

	private void handleAttributes(Context context, AttributeSet attrs) {
		TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeView);
		padding = array.getDimensionPixelSize(R.styleable.BadgeView_badgePadding, (int) ViewUtils.dpToPx(context, padding));
		backgroundColor = array.getColor(R.styleable.BadgeView_badgeBackgroundColor, backgroundColor);
		animationDuration = array.getInteger(R.styleable.BadgeView_badgeAnimationDuration, animationDuration);
		array.recycle();
	}

	@Override public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		background.setColor(backgroundColor);
		invalidate();
	}

	private void config() {
		setPadding(padding, padding, padding, padding);
		background.setColor(backgroundColor);
	}

	protected void setValue(@NonNull final IValue newValue, boolean animate) {
		setValue(newValue, animate, animationDuration);
	}

	protected void setValue(@NonNull final IValue newValue, boolean animate, long duration) {

		newValue.calculateBounds();
		if (getVisibility() != VISIBLE) animate = false;
		if (valueCenter.equals(newValue)) return;
		bufferValue = newValue;
		if (isAnimating) return;

		if (valueCenter.compareBounds(newValue)) changeBackgroundSize(newValue, animate, duration);

		if (animate) {

			final boolean isBigger = valueCenter.compare(newValue);
			IValue<?> nextValue;
			if (isBigger) {
				valueBottom = newValue;
				nextValue = valueBottom;
			} else {
				valueTop = newValue;
				nextValue = valueTop;
			}

			float offset = nextValue.getBounds().height() + getPaddingTop() + getPaddingBottom();

			animateShiftAndSetValue(newValue, isBigger ? offset * -1 : offset, animationDuration);
		} else {
			valueCenter = newValue;
			invalidate();
		}
	}

	private void changeBackgroundSize(final IValue<?> newValue, boolean animate, long duration) {

		if (animate) {

			final int currentWidth = valueCenter.getBounds().width();
			final int currentHeight = valueCenter.getBounds().height();
			int newWidth;
			int newHeight;

			newWidth = newValue.getBounds().width();
			newHeight = newValue.getBounds().height();

			ChangeLayoutSizeAnimation anim = new ChangeLayoutSizeAnimation(this, duration);
			anim.setInterpolator(interpolator);
			RectF bounds = background.getBounds();
			anim.setWidth((int) (newWidth + bounds.width() - currentWidth));
			anim.setHeight((int) (newHeight + bounds.height() - currentHeight));
			startAnimation(anim);
		} else {
			getLayoutParams().width = newValue.getBounds().width() + getPaddingLeft() + getPaddingRight();
			getLayoutParams().height = newValue.getBounds().height() + getPaddingTop() + getPaddingBottom();
			requestLayout();
		}
	}

	private void animateShiftAndSetValue(final IValue<?> newValue, float newHeight, long duration) {

		finalHeight = newHeight;
		final ValueAnimator shiftAnimator = ValueAnimator.ofFloat(0, newHeight);

		shiftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override public void onAnimationUpdate(ValueAnimator animation) {
				animYShift = (float) animation.getAnimatedValue();
				invalidate();
			}
		});

		shiftAnimator.addListener(new AbstractAnimatorListener() {
			@Override public void onAnimationStart(Animator animation) {
				isAnimating = true;
			}

			@Override public void onAnimationEnd(final Animator animation) {
				isAnimating = false;
				animYShift = 0;
				valueCenter = newValue.copy();
				invalidate();
				if (!bufferValue.equals(newValue)) {
					setValue(bufferValue, true);
				}

				if (animations != null && !animations.isEmpty()) {
					handleAnimation(animations.pop());
				} else {
					handleAnimationListener();
				}

			}
		});

		shiftAnimator.setDuration(duration);
		shiftAnimator.setInterpolator(interpolator);
		shiftAnimator.start();
	}

	private void handleAnimationListener() {
		if (animationListener != null) {
			animationListener.onAnimationEnd();
			animationListener = null;
		}
	}

	private void handleAnimation(ValueAnimation a) {
		if (a instanceof Delay) {
			postDelayed(new Runnable() {
				@Override public void run() {
					if (animations != null && !animations.isEmpty()) {
						handleAnimation(animations.pop());
					} else {
						handleAnimationListener();
					}
				}
			}, a.duration);
		} else {
			setValue(a.value, true, a.duration);
		}
	}

	public void hide() {
		hide(null);
	}

	public void hide(Animator.AnimatorListener listener) {
		isHidden = true;
		if (getVisibility() == VISIBLE) {
			animate()
					.setInterpolator(interpolator)
					.setListener(listener)
					.setDuration(animationDuration)
					.scaleX(0)
					.scaleY(0)
					.start();
		} else {
			setScaleX(0);
			setScaleY(0);
		}
	}

	public void show() {
		show(null);
	}

	public void show(Animator.AnimatorListener listener) {
		isHidden = false;
		if (getVisibility() != VISIBLE) {
			setScaleX(0);
			setScaleY(0);
			setVisibility(VISIBLE);
		}

		animate()
				.setInterpolator(interpolator)
				.setListener(listener)
				.setDuration(animationDuration)
				.scaleX(1)
				.scaleY(1)
				.start();
	}

	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		background.onLayout(left, top, right, bottom);
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		background.draw(canvas);
		background.clip(canvas);
		if (finalHeight == 0) finalHeight = background.getBounds().height();
		valueTop.onDraw(canvas, animYShift, background.getBounds(), finalHeight, -1);
		valueCenter.onDraw(canvas, animYShift, background.getBounds(), finalHeight, 0);
		valueBottom.onDraw(canvas, animYShift, background.getBounds(), finalHeight, 1);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int valueWidth = valueCenter.getBounds().width();
		int valueHeight = valueCenter.getBounds().height();

		int resultWidth = valueWidth + getPaddingLeft() + getPaddingRight();
		int resultHeight = valueHeight + getPaddingBottom() + getPaddingTop();

		if (isAnimating) {
			if (widthMode == MeasureSpec.EXACTLY) {
				resultWidth = parentWidth;
			}
			if (heightMode == MeasureSpec.EXACTLY) {
				resultHeight = parentHeight;
			}
		}

		setMeasuredDimension(resultWidth, resultHeight);
	}

	protected void setValues(LinkedList<ValueAnimation> animations) {
		this.animations = animations;
		ValueAnimation animation = animations.pop();
		setValue(animation.value, true, animation.duration);
	}

	@Override protected Parcelable onSaveInstanceState() {
		return new State(super.onSaveInstanceState(), valueCenter, isHidden, background.getColor());
	}

	@Override protected void onRestoreInstanceState(Parcelable parc) {
		State state = (State) parc;
		super.onRestoreInstanceState(state.getSuperState());
		valueCenter = state.value;
		isHidden = state.isHidden;
		background.setColor(state.backgroundColor);

		if (isHidden) {
			setScaleX(0);
			setScaleY(0);
		}


	}

	public void setAnimationListener(IAnimationListener animationListener) {
		this.animationListener = animationListener;
	}

	static class Delay extends ValueAnimation {
		public Delay(long duration) {
			super(null, duration);
		}
	}

	static class ValueAnimation {

		private IValue value;
		private long duration;

		public ValueAnimation(IValue value, long duration) {
			this.value = value;
			this.duration = duration;
		}
	}

	private static class State extends BaseSavedState {

		private IValue value;
		private boolean isHidden;
		private int backgroundColor;

		public State(Parcel source) {
			super(source);
			value = (IValue) source.readValue(IValue.class.getClassLoader());
			isHidden = source.readByte() != 0;
			backgroundColor = source.readInt();
		}

		public State(Parcelable superState, IValue value, boolean isHidden, int backgroundColor) {
			super(superState);
			this.value = value;
			this.isHidden = isHidden;
			this.backgroundColor = backgroundColor;
		}

		@Override public void writeToParcel(@NonNull Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeValue(value);
			out.writeByte((byte) (isHidden ? 1 : 0));
			out.writeInt(backgroundColor);
		}

		public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {

			public State createFromParcel(Parcel in) {
				return new State(in);
			}

			public State[] newArray(int size) {
				return new State[size];
			}

		};
	}

}
