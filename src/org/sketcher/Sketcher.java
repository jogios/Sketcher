package org.sketcher;

import org.sketcher.style.StylesFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout.LayoutParams;

public class Sketcher extends Activity {
	private static final int MENU_CLEAR = 0x2001;
	private static final int MENU_SAVE = 0x2002;
	private static final int MENU_SEND = 0x2003;

	private Surface surface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		surface = new Surface(this);

		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);

		setContentView(surface, params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_CLEAR, 0, "Clear");
		menu.add(0, MENU_SAVE, 0, "Save");
		menu.add(0, MENU_SEND, 0, "Send");
		menu.add(0, StylesFactory.SKETCHY, 0, "Sketchy");
		menu.add(0, StylesFactory.SHADED, 0, "Shaded");
		menu.add(0, StylesFactory.CHROME, 0, "Chrome");
		menu.add(0, StylesFactory.FUR, 0, "Fur");
		menu.add(0, StylesFactory.LONGFUR, 0, "Longfur");
		menu.add(0, StylesFactory.WEB, 0, "Web");
		menu.add(0, StylesFactory.SQUARES, 0, "Squares");
		menu.add(0, StylesFactory.RIBBON, 0, "Ribbon");
		menu.add(0, StylesFactory.CIRCLES, 0, "Circles");
		menu.add(0, StylesFactory.GRID, 0, "Grid");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CLEAR:
			surface.clearBitmap();
			return true;
		case MENU_SAVE:
			surface.save();
			return true;
		case MENU_SEND:
			surface.send();
			return true;

		default:
			surface.setStyle(StylesFactory.getStyle(item.getItemId()));
			return true;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		surface.saveState();
	}
}
