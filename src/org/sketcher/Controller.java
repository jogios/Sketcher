package org.sketcher;

import org.sketcher.style.StylesFactory;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class Controller {
	Style style = StylesFactory.getStyle(StylesFactory.SKETCHY);
	private boolean toStroke;
	private boolean toDraw;

	public void draw(Canvas c) {
		if (toDraw) {
			style.draw(c);
			toDraw = false;
		}
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			toStroke = false;
			style.strokeEnd(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_DOWN:
			toStroke = true;
			style.strokeStart(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			if (toStroke) {
				style.stroke(event.getX(), event.getY());
				toDraw = true;
			}
			break;
		}
		return true;
	}
}
