package org.sketcher;

import org.sketcher.colorpicker.Picker;
import org.sketcher.colorpicker.PickerDialog;
import org.sketcher.style.StylesFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class Sketcher extends Activity {
	private static final short GROUP_BRUSHES = 0x1000;
	private static final short MENU_CLEAR = 0x2001;
	private static final short MENU_SAVE = 0x2002;
	private static final short MENU_SHARE = 0x2003;
	private static final short MENU_COLOR = 0x2004;
	private static final short MENU_ABOUT = 0x2005;

	private Surface surface;
	private FileHelper fileHelper = new FileHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// register nullwire exception handler which sends crash reports to
		// http://trace.nullwire.com/. Disabled since it's down
		// ExceptionHandler.register(this);

		setContentView(R.layout.main);
		surface = (Surface) findViewById(R.id.surface);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (fileHelper.isSaved) {
			return;
		}
		// wrapped to a new thread since it can be killed due to time limits for
		// #onPause() method
		new Thread() {
			@Override
			public void run() {
				fileHelper.saveBitmap();
			}
		}.run();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fileHelper.isSaved = false;
		getSurface().setInitialBitmap(fileHelper.getSavedBitmap());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_SAVE, 0, R.string.save).setIcon(
				android.R.drawable.ic_menu_save);
		menu.add(0, MENU_SHARE, 0, R.string.send).setIcon(
				android.R.drawable.ic_menu_send);
		menu.add(0, MENU_CLEAR, 0, R.string.clear).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(0, MENU_COLOR, 0, R.string.color).setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_ABOUT, 0, R.string.about).setIcon(
				android.R.drawable.ic_menu_info_details);
		SubMenu subMenu = menu.addSubMenu(R.string.brushes).setIcon(
				android.R.drawable.ic_menu_edit);
		subMenu.add(GROUP_BRUSHES, StylesFactory.ERASER, 0, R.string.eraser);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SKETCHY, 0, R.string.sketchy);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SIMPLE, 0, R.string.simple);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SHADED, 0, R.string.shaded);
		subMenu.add(GROUP_BRUSHES, StylesFactory.CHROME, 0, R.string.chrome);
		subMenu.add(GROUP_BRUSHES, StylesFactory.FUR, 0, R.string.fur);
		subMenu.add(GROUP_BRUSHES, StylesFactory.LONGFUR, 0, R.string.longfur);
		subMenu.add(GROUP_BRUSHES, StylesFactory.WEB, 0, R.string.web);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SQUARES, 0, R.string.squares);
		subMenu.add(GROUP_BRUSHES, StylesFactory.RIBBON, 0, R.string.ribbon);
		subMenu.add(GROUP_BRUSHES, StylesFactory.CIRCLES, 0, R.string.circles);
		subMenu.add(GROUP_BRUSHES, StylesFactory.GRID, 0, R.string.grid);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == GROUP_BRUSHES) {
			getSurface().setStyle(StylesFactory.getStyle(item.getItemId()));
			return true;
		}

		switch (item.getItemId()) {
		case MENU_CLEAR:
			clearCanvas();
			return true;
		case MENU_SAVE:
			fileHelper.saveToSD();
			return true;
		case MENU_SHARE:
			fileHelper.share();
			return true;
		case MENU_ABOUT:
			showAboutDialog();
			return true;
		case MENU_COLOR:
			new PickerDialog(this, new Picker.OnColorChangedListener() {
				@Override
				public void colorChanged(Paint color) {
					getSurface().setPaintColor(color);
				}
			}, getSurface().getPaintColor()).show();
			return true;

		default:
			return false;
		}
	}

	private void clearCanvas() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.are_you_sure)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								getSurface().clearBitmap();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.create().show();
	}

	private void showAboutDialog() {
		Dialog dialog = new AboutDialog(this);
		dialog.show();
	}

	Surface getSurface() {
		return surface;
	}
}
