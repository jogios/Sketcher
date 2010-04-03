package org.sketcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.sketcher.style.StylesFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class Surface extends SurfaceView implements Callback {
	private static final String STATE_FILE = "backup";

	private final class DrawThread extends Thread {
		@Override
		public void run() {
			SurfaceHolder surfaceHolder = getHolder();
			while (bitmap == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			bitmap.eraseColor(Color.WHITE);
			Canvas drawArea = new Canvas(bitmap);

			try {
				FileInputStream fis = getContext().openFileInput(STATE_FILE);
				Bitmap savedBitmap = BitmapFactory.decodeStream(fis);
				drawArea.drawBitmap(savedBitmap, 0, 0, null);
			} catch (FileNotFoundException e) {
			}

			while (mRun) {
				Canvas canvas = null;
				try {
					canvas = surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						canvas.drawBitmap(bitmap, 0, 0, null);
						controller.draw(drawArea);
					}
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}

	private boolean mRun;
	private Thread drawThread;
	private Controller controller;

	private Bitmap bitmap;

	public Surface(Context context) {
		super(context);

		controller = new Controller();
		getHolder().addCallback(this);
		setFocusable(true);
	}

	public void setStyle(Style style) {
		controller.setStyle(style);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mRun = true;
		drawThread = new DrawThread();
		drawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mRun = false;
		while (true) {
			try {
				drawThread.join();
				bitmap = null;
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return controller.onTouchEvent(event);
	}

	public void clearBitmap() {
		bitmap.eraseColor(Color.WHITE);
		StylesFactory.clearCache();
	}

	public void saveState() {
		try {
			FileOutputStream fos = getContext().openFileOutput(STATE_FILE,
					Context.MODE_PRIVATE);
			bitmap.compress(CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		String path = "/sdcard/sketcher/";
		String filename = "image_";
		String extension = ".png";

		boolean exists = new File(path).exists();
		if (!exists) {
			new File(path).mkdirs();
		}

		int suffix = 1;

		while (new File(path + filename + suffix + extension).exists()) {
			suffix++;
		}

		final String fileName = path + filename + suffix + extension;
		new AsyncTask<Void, Integer, Long>() {
			@Override
			protected Long doInBackground(Void... params) {
				try {
					FileOutputStream fos = new FileOutputStream(fileName);
					bitmap.compress(CompressFormat.PNG, 100, fos);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Long result) {
				Toast.makeText(getContext(), "Image saved to SD",
						Toast.LENGTH_SHORT).show();
			}

		}.execute();
	}
}
