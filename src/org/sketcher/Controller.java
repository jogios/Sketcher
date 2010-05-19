package org.sketcher;

import org.sketcher.style.StylesFactory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class Controller {
	private Style style;
	private Canvas canvas;
	private boolean toDraw = false;
	private int color = Color.BLACK;

	{
		clear();
	}

	public void draw(Canvas c) {
		if (toDraw) {
			style.draw(c);
		}
	}

	public void setStyle(Style style) {
		toDraw = false;
		style.setColor(color);
		this.style = style;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			toDraw = true;
			style.strokeStart(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			style.stroke(canvas, event.getX(), event.getY());
			break;
		}
		return true;
	}

	public void clear() {
		toDraw = false;
		StylesFactory.clearCache();
		setStyle(StylesFactory.getCurrentStyle());
	}

	public void setPaintColor(int color) {
		this.color = color;
		style.setColor(color);
	}

	public int getPaintColor() {
		return color;
	}
}
