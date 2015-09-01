package su.levenetc.android.badgeview.values;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class BitmapValue extends IValue<Bitmap> implements Parcelable {

	private Paint paint = new Paint();

	{
		paint.setAntiAlias(true);
	}

	public BitmapValue(Bitmap bitmap) {
		setValue(bitmap);
	}

	public BitmapValue(Parcel in) {
		setValue((Bitmap) in.readParcelable(Bitmap.class.getClassLoader()));
	}

	@Override public void onDraw(Canvas canvas, float yPosition, RectF clipRect, float finalHeight, int index) {

		final float x = clipRect.width() / 2 - value.getWidth() / 2f;
		float y;
		final float yCenter = value.getHeight() / 2f;
		final float valueHalfHeight = value.getHeight() / 2f;
		final float clipRectHeight = clipRect.height();
		final float fraction = yPosition / finalHeight;//0.0f -> 1.0f
		final float backFraction = Math.abs(1 - fraction);//1.0f -> 0.0f

		if (index == -1) {
			y = clipRectHeight / 2 - yCenter - clipRectHeight;
		} else if (index == 1) {
			//bottom
			y = clipRectHeight / 2 - yCenter + clipRectHeight + backFraction * valueHalfHeight;
		} else {
			y = clipRectHeight / 2 - yCenter - fraction * valueHalfHeight;
		}

		canvas.drawBitmap(value, x, y + yPosition, paint);
	}

	@Override public boolean compare(IValue<?> value) {
		return true;
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BitmapValue bValue = (BitmapValue) o;

		return value.sameAs(bValue.value) || !(value != null ? !value.equals(bValue.value) : bValue.value != null);
	}

	@Override public IValue<Bitmap> copy() {
		return new BitmapValue(value);
	}

	@Override public void calculateBounds() {
		bounds.set(0, 0, value.getWidth(), value.getHeight());
	}

	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(value, 0);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public BitmapValue createFromParcel(Parcel in) {
			return new BitmapValue(in);
		}

		public BitmapValue[] newArray(int size) {
			return new BitmapValue[size];
		}
	};

}
