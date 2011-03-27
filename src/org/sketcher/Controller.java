package org.sketcher;

import org.sketcher.style.StylesFactory;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class Controller implements View.OnTouchListener {
	private Style style;
	private Canvas canvas = new Canvas();
	private boolean toDraw = false;
	private Paint mColor = new Paint();

	{
		clear();
	}

	public void draw() {
		if (toDraw) {
			style.draw(canvas);
		}
	}

	public void setStyle(Style style) {
		toDraw = false;
		style.setColor(mColor.getColor());
		this.style = style;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
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

	public void setPaintColor(Paint color) {
		mColor = color;
		style.setColor(color.getColor());
	}

	public Paint getPaintColor() {
		return mColor;
	}

}
