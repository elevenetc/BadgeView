package su.levenetc.android.badgeview.animations;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;


/**
 * Created by Eugene Levenetc.
 */
public class ChangeLayoutSizeAnimation extends Animation {

	private final int initialHeight;
	private int width = -1;
	private int height = -1;
	private View view;
	private final int initialWidth;

	public ChangeLayoutSizeAnimation(View view, long duration) {
		this.view = view;
		setDuration(duration);
		initialWidth = view.getWidth();
		initialHeight = view.getHeight();
	}

	public void setWidth(int width) {
		this.width = width - initialWidth;
	}

	public void setHeight(int height) {
		this.height = height - initialHeight;
	}

	@Override protected void applyTransformation(float interpolatedTime, Transformation t) {

		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		if (width != -1) {
			int newWidth = (int) (width * interpolatedTime);
			layoutParams.width = initialWidth + newWidth;
		}

		if (height != -1) {
			int newHeight = (int) (height * interpolatedTime);
			layoutParams.height = initialHeight + newHeight;
		}
		view.requestLayout();
	}

	@Override public boolean willChangeBounds() {
		return true;
	}
}
