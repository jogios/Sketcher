package org.sketcher.style;

import java.util.ArrayList;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

class ChromeStyle implements Style {
	private float prevX;
	private float prevY;

	private ArrayList<PointF> points = new ArrayList<PointF>();
	private int count = 0;

	private Paint paint = new Paint();
	private Paint randPaint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(30);
		paint.setAntiAlias(true);

		randPaint.setAntiAlias(true);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
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

			if (length < 1000) {
				randPaint.setARGB(35, (int) (Math.random() * 255), (int) (Math
						.random() * 255), (int) (Math.random() * 255));
				c
						.drawLine(_point.x + (dx * 0.2F), _point.y
								+ (dy * 0.2F), point.x - (dx * 0.2F), point.y
								- (dy * 0.2F), randPaint);
			}
		}

		prevX = x;
		prevY = y;

		count++;
	}

	@Override
	public void strokeStart(float x, float y) {
		prevX = x;
		prevY = y;
	}

	@Override
	public void draw(Canvas c) {
	}

	@Override
	public void setColor(int color) {
		paint.setColor(color);
		paint.setAlpha(30);
	}
}
