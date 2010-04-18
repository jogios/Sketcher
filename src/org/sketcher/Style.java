package org.sketcher;

import android.graphics.Canvas;

public interface Style {
	public void draw(Canvas c);

	public void strokeStart(float x, float y);

	public void stroke(float x, float y);
}
