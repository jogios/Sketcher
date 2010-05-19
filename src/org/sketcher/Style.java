package org.sketcher;

import android.graphics.Canvas;

public interface Style {
	public void strokeStart(float x, float y);

	public void stroke(Canvas c, float x, float y);

	public void draw(Canvas c);

	public void setColor(int color);
}
