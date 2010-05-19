package org.sketcher.style;

import org.sketcher.Style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class SquaresStyle implements Style {
	private float prevX;
	private float prevY;

	private Paint paint = new Paint();

	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
	}

	@Override
	public void stroke(Canvas c, float x, float y) {
		float dx = x - prevX;
		float dy = y - prevY;

		float alpha = 1.57079633F;

		float ax = (float) (Math.cos(alpha) * dx - Math.sin(alpha) * dy);
		float ay = (float) (Math.sin(alpha) * dx + Math.cos(alpha) * dy);

		c.drawLine(prevX - ax, prevY - ay, prevX + ax, prevY + ay, paint);
		c.drawLine(prevX + ax, prevY + ay, x + ax, y + ay, paint);
		c.drawLine(x + ax, y + ay, x - ax, y - ay, paint);
		c.drawLine(x - ax, y - ay, prevX - ax, prevY - ay, paint);

		prevX = x;
		prevY = y;
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
		paint.setAlpha(100);
	}
}
