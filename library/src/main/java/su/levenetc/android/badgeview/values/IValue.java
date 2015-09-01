/*
 * Copyright 2015 Eugene Levenetc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package su.levenetc.android.badgeview.values;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Eugene Levenetc.
 */
public abstract class IValue<T> {

	/**
	 * Rectangle which represents visible area of value.
	 * Should be in {@link IValue#calculateBounds}
	 */
	protected final Rect bounds = new Rect();
	protected T value;

	public Rect getBounds() {
		return bounds;
	}

	public void setValue(T value) {
		this.value = value;
		calculateBounds();
	}

	public T getValue() {
		return value;
	}

	public boolean compareBounds(IValue<?> value) {
		return value.getBounds().width() != getBounds().width() ||
				value.getBounds().height() != getBounds().height();
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IValue<?> iValue = (IValue<?>) o;

		return !(value != null ? !value.equals(iValue.value) : iValue.value != null);

	}

	@Override public int hashCode() {
		return value != null ? value.hashCode() : 0;
	}

	/**
	 * Defines vertical direction of animation.
	 * Called by {@link su.levenetc.android.badgeview.AbstractBadgeView}
	 *
	 * @return true ? shifts up : shifts down
	 */
	public abstract boolean compare(IValue<?> value);

	public abstract IValue<T> copy();

	public abstract void calculateBounds();

	/**
	 * Calls whenever view is invalidated.
	 * @param yPosition Animated value from 0 to height of the view
	 * @param clipRect Size of view.
	 * @param finalHeight Final value of yPosition
	 * @param index -1: top, 0: middle, 1: bottom
	 */
	public abstract void onDraw(Canvas canvas, float yPosition, RectF clipRect, float finalHeight, int index);

}
