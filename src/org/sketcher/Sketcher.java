package org.sketcher;

import java.io.File;
import java.io.FileNotFoundException;

import org.sketcher.ColorPickerDialog.OnColorChangedListener;
import org.sketcher.style.StylesFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class Sketcher extends Activity {
	private static final short GROUP_BRUSHES = 0x1000;
	private static final short MENU_CLEAR = 0x2001;
	private static final short MENU_SAVE = 0x2002;
	private static final short MENU_SHARE = 0x2003;
	private static final short MENU_COLOR = 0x2004;

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

		menu.add(0, MENU_SAVE, 0, R.string.save).setIcon(R.drawable.save);
		menu.add(0, MENU_SHARE, 0, R.string.send).setIcon(R.drawable.send);
		menu.add(0, MENU_CLEAR, 0, R.string.clear).setIcon(R.drawable.clear);
		menu.add(0, MENU_COLOR, 0, R.string.color).setIcon(R.drawable.color);
		SubMenu subMenu = menu.addSubMenu(R.string.brushes).setIcon(
				R.drawable.brushes);
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
			surface.setStyle(StylesFactory.getStyle(item.getItemId()));
			return true;
		}

		switch (item.getItemId()) {
		case MENU_CLEAR:
			surface.clearBitmap();
			return true;
		case MENU_SAVE:
			saveToSD();
			return true;
		case MENU_SHARE:
			share();
			return true;
		case MENU_COLOR:
			new ColorPickerDialog(this, new OnColorChangedListener() {
				@Override
				public void colorChanged(int color) {
					surface.setPaintColor(color);
				}
			}, surface.getPaintColor()).show();
			return true;

		default:
			return false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		surface.saveState();
	}

	private void share() {
		if (!checkStorage()) {
			return;
		}

		new AsyncTask<Void, Void, String>() {
			private ProgressDialog dialog = ProgressDialog.show(Sketcher.this,
					"", getString(R.string.please_wait), true);

			protected String doInBackground(Void... none) {
				String fileName = getSDDir() + "tmp.png";
				return saveBitmap(fileName);
			}

			protected void onPostExecute(String fileName) {
				dialog.hide();
				Uri uri = Uri.fromFile(new File(fileName));

				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("image/png");
				i.putExtra(Intent.EXTRA_STREAM, uri);
				startActivity(Intent.createChooser(i,
						getString(R.string.send_image_to)));
			}
		}.execute();
	}

	private boolean checkStorage() {
		String externalStorageState = Environment.getExternalStorageState();
		if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, R.string.sd_card_is_not_available,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void saveToSD() {
		if (!checkStorage()) {
			return;
		}

		new AsyncTask<Void, Void, String>() {
			private ProgressDialog dialog = ProgressDialog.show(Sketcher.this,
					"", getString(R.string.saving_to_sd_please_wait), true);

			protected String doInBackground(Void... none) {
				surface.getThread().pauseDrawing();
				return saveBitmap(getUniqueFilePath());
			}

			protected void onPostExecute(String fileName) {
				Uri uri = Uri.fromFile(new File(fileName));
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						uri));

				dialog.hide();
				surface.getThread().resumeDrawing();
			}
		}.execute();
	}

	private String saveBitmap(String fileName) {
		try {
			surface.saveBitmap(fileName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		return fileName;
	}

	private String getUniqueFilePath() {
		String path = getSDDir();
		String filename = "image_";
		String extension = ".png";

		int suffix = 1;

		while (new File(path + filename + suffix + extension).exists()) {
			suffix++;
		}

		return path + filename + suffix + extension;
	}

	private String getSDDir() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/sketcher/";

		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}

		return path;
	}
}
