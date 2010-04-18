package org.sketcher.style;

import java.util.ArrayList;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

class SketchyStyle implements Style {
	private float prevX;
	private float prevY;

	private ArrayList<PointF> points = new ArrayList<PointF>();
	private int count = 0;

	private Paint paint = new Paint();

	private float x;
	private float y;

	{
		paint.setARGB(30, 0, 0, 0);
		paint.setAntiAlias(true);
	}

	@Override
	public void draw(Canvas c) {
		points.add(new PointF(x, y));

		c.drawLine(prevX, prevY, x, y, paint);

		float dx = 0;
		float dy = 0;
		float length = 0;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);
			PointF _point = points.get(count);
			dx = point.x - _point.x;
			dy = point.y - _point.y;

			length = dx * dx + dy * dy;

			if (length < 4000 && Math.random() > length / 2000) {
				c.drawLine(_point.x + (dx * 0.3F), _point.y + (dy * 0.3F),
						point.x - (dx * 0.3F), point.y - (dy * 0.3F), paint);
			}
		}

		prevX = x;
		prevY = y;

		count++;
	}

	@Override
	public void stroke(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void strokeStart(float x, float y) {
		prevX = x;
		prevY = y;
	}
}
