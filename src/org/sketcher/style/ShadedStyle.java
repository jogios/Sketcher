package org.sketcher.style;

import java.util.ArrayList;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

class ShadedStyle implements Style {
	private ArrayList<PointF> points = new ArrayList<PointF>();
	private int count = 0;

	private Paint paint = new Paint();

	private float x;
	private float y;

	{
		paint.setAntiAlias(true);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void draw(Canvas c) {
		points.add(new PointF(x, y));

		float dx = 0;
		float dy = 0;
		int length = 0;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);
			PointF _point = points.get(count);

			dx = point.x - _point.x;
			dy = point.y - _point.y;

			length = (int) (dx * dx + dy * dy);

			if (length < 1000) {
				paint.setARGB(((1 - (length / 1000)) * 30), 0, 0, 0);
				c.drawLine(_point.x, _point.y, point.x, point.y, paint);
			}
		}

		count++;
	}

	@Override
	public void stroke(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void strokeEnd(float x, float y) {
	}

	@Override
	public void strokeStart(float x, float y) {
	}
}
