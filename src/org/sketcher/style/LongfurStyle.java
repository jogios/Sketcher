package org.sketcher.style;

import java.util.ArrayList;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

class LongfurStyle implements Style {
	private ArrayList<PointF> points = new ArrayList<PointF>();
	private int count = 0;

	private Paint paint = new Paint();

	{
		paint.setARGB(25, 0, 0, 0);
		paint.setAntiAlias(true);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		points.add(new PointF(x, y));

		float dx = 0;
		float dy = 0;
		float rand = 0;
		float length = 0;

		for (int i = 0, max = points.size(); i < max; i++) {
			PointF point = points.get(i);
			PointF _point = points.get(count);

			dx = point.x - _point.x;
			dy = point.y - _point.y;

			rand = (float) -Math.random();
			length = dx * dx + dy * dy;

			if (length < 4000 && Math.random() > length / 4000) {
				c.drawLine(_point.x + (dx * rand), _point.y + (dy * rand),
						point.x - (dx * rand) + (float) Math.random() * 2,
						point.y - (dy * rand) + (float) Math.random() * 2,
						paint);
			}
		}

		count++;
	}

	@Override
	public void strokeStart(float x, float y) {
	}
	
	@Override
	public void draw(Canvas c) {
	}
}
