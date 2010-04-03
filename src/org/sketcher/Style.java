package org.sketcher;

import android.graphics.Canvas;

public interface Style {
	public void draw(Canvas c);

	public void destroy();

	public void strokeStart(float x, float y);

	public void stroke(float x, float y);

	public void strokeEnd(float x, float y);
}
