package org.sketcher.style;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Paint;

class CirclesStyle implements Style {
	private float prevX;
	private float prevY;

	private Paint paint = new Paint();

	private float x;
	private float y;

	{
		paint.setARGB(50, 0, 0, 0);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
	}

	@Override
	public void draw(Canvas c) {
		float dx = x - prevX;
		float dy = y - prevY;

		int dxy = (int) (Math.sqrt(dx * dx + dy * dy) * 2);

		int gridx = (int) (Math.floor(x / 50) * 50 + 25);
		int gridy = (int) (Math.floor(y / 50) * 50 + 25);

		int rand = (int) (Math.floor(Math.random() * 9) + 1);
		int radius = dxy / rand;

		for (int i = 0; i < rand; i++) {
			c.drawCircle(gridx, gridy, (rand - i) * radius, paint);
		}

		prevX = x;
		prevY = y;
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
