package org.sketcher.style;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Paint;

class GridStyle implements Style {
	private float x;
	private float y;

	private Paint paint = new Paint();

	{
		paint.setARGB(25, 0, 0, 0);
		paint.setAntiAlias(true);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void draw(Canvas c) {
		float gridx = Math.round(x / 100) * 100;
		float gridy = Math.round(y / 100) * 100;

		float dx = (gridx - x) * 10;
		float dy = (gridy - y) * 10;

		for (int i = 0; i < 50; i++) {
			c.drawLine(x + (float) Math.random() * dx, y
					+ (float) Math.random() * dy, gridx, gridy, paint);
		}
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
