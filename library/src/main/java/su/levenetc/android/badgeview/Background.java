package su.levenetc.android.badgeview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by Eugene Levenetc.
 */
public class Background {

    private final Path path = new Path();
    private final Paint fillPaint = new Paint();
    private final RectF bounds = new RectF();

    public Background() {
        fillPaint.setAntiAlias(true);
    }

    public RectF getBounds() {
        return bounds;
    }

    public void draw(Canvas canvas) {
        path.reset();
        float radius = Math.min(bounds.width(), bounds.height()) / 2;
        path.addRoundRect(bounds, radius, radius, Path.Direction.CW);
        canvas.drawPath(path, fillPaint);
    }

    public void clip(Canvas canvas) {
        canvas.clipPath(path);
    }

    public void onLayout(int left, int top, int right, int bottom) {
        bounds.left = 0;
        bounds.top = 0;
        bounds.right = right - left;
        bounds.bottom = bottom - top;
    }

    public void setColor(int backgroundColor) {
        fillPaint.setColor(backgroundColor);
    }

    public int getColor() {
        return fillPaint.getColor();
    }
}