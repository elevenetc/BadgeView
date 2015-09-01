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
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import su.levenetc.android.badgeview.utils.StringUtils;

/**
 * Created by Eugene Levenetc.
 */
public class TextValue extends IValue<CharSequence> implements Parcelable {

	private Paint paint;

	public TextValue(@NonNull CharSequence value, Paint paint) {
		this.paint = paint;
		setValue(value);
	}

	public TextValue(@NonNull Number value, Paint paint) {
		this.paint = paint;
		setValue(value.toString());
	}

	public void setTextColor(int color) {
		paint.setColor(color);
	}

	public TextValue(Parcel in) {

		String value = in.readString();
		paint.setColor(in.readInt());
		paint.setTextSize(in.readFloat());
		setValue(value);
	}

	@Override public void setValue(CharSequence value) {
		super.setValue(value);
	}


	@Override public void onDraw(Canvas canvas, float yPosition, RectF clipRect, float finalHeight, int index) {

		float x = clipRect.width() / 2;
		float y;
		final float yCenter = bounds.exactCenterY();
		final float clipRectHeight = clipRect.height();

		if (index == -1) {
			y = clipRectHeight / 2 - yCenter - clipRectHeight;
		} else if (index == 1) {
			y = clipRectHeight / 2 - yCenter + clipRectHeight;
		} else {
			y = clipRectHeight / 2 - yCenter;
		}

		canvas.drawText(value, 0, value.length(), x, y + yPosition, paint);
	}

	@Override public boolean compare(IValue<?> value) {

		if (StringUtils.isNumber(this.value) && StringUtils.isNumber(value)) {
			return getNumber() < ((TextValue) value).getNumber();
		} else {
			return true;
		}

	}

	public double getNumber() {
		return Double.parseDouble(value.toString());
	}

	@Override public IValue<CharSequence> copy() {
		return new TextValue(value, paint);
	}

	@Override public void calculateBounds() {
		paint.getTextBounds(value.toString(), 0, value.length(), bounds);
	}

	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(value.toString());
		dest.writeInt(paint.getColor());
		dest.writeFloat(paint.getTextSize());
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public TextValue createFromParcel(Parcel in) {
			return new TextValue(in);
		}

		public TextValue[] newArray(int size) {
			return new TextValue[size];
		}
	};

	public void setPaint(Paint paint) {
		this.paint = paint;
		calculateBounds();
	}

	public Paint getPaint() {
		return paint;
	}
}
