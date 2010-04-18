package org.sketcher;

import java.io.File;
import java.io.FileNotFoundException;

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
	private static final short MENU_SEND = 0x2003;

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

		menu.add(0, MENU_SAVE, 0, "Save").setIcon(R.drawable.save);
		menu.add(0, MENU_SEND, 0, "Send").setIcon(R.drawable.send);
		menu.add(0, MENU_CLEAR, 0, "Clear").setIcon(R.drawable.clear);
		SubMenu subMenu = menu.addSubMenu("Brushes")
				.setIcon(R.drawable.brushes);
		subMenu.add(GROUP_BRUSHES, StylesFactory.SKETCHY, 0, "Sketchy");
		subMenu.add(GROUP_BRUSHES, StylesFactory.SHADED, 0, "Shaded");
		subMenu.add(GROUP_BRUSHES, StylesFactory.CHROME, 0, "Chrome");
		subMenu.add(GROUP_BRUSHES, StylesFactory.FUR, 0, "Fur");
		subMenu.add(GROUP_BRUSHES, StylesFactory.LONGFUR, 0, "Longfur");
		subMenu.add(GROUP_BRUSHES, StylesFactory.WEB, 0, "Web");
		subMenu.add(GROUP_BRUSHES, StylesFactory.SQUARES, 0, "Squares");
		subMenu.add(GROUP_BRUSHES, StylesFactory.RIBBON, 0, "Ribbon");
		subMenu.add(GROUP_BRUSHES, StylesFactory.CIRCLES, 0, "Circles");
		subMenu.add(GROUP_BRUSHES, StylesFactory.GRID, 0, "Grid");

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
		case MENU_SEND:
			sendImage();
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

	private void sendImage() {
		File file = getFileStreamPath(Surface.STATE_FILE);
		Uri uri = Uri.fromFile(file);

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("image/png");
		i.putExtra(Intent.EXTRA_STREAM, uri);
		i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivity(Intent.createChooser(i, "Send Image To:"));
	}

	private void saveToSD() {
		String externalStorageState = Environment.getExternalStorageState();
		if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			Toast
					.makeText(this, "SD card is not available",
							Toast.LENGTH_SHORT).show();
			return;
		}

		final ProgressDialog dialog = ProgressDialog.show(this, "",
				"Saving to SD. Please wait...", true);

		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... urls) {
				surface.getThread().pauseDrawing();

				String path = "/sdcard/sketcher/";
				String filename = "image_";
				String extension = ".png";

				if (!new File(path).exists()) {
					new File(path).mkdirs();
				}

				int suffix = 1;

				while (new File(path + filename + suffix + extension).exists()) {
					suffix++;
				}

				final String fileName = path + filename + suffix + extension;

				try {
					surface.saveBitmap(fileName);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				surface.getThread().resumeDrawing();
				dialog.hide();
			}
		}.execute();
	}
}
