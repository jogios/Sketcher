package org.sketcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class Surface extends SurfaceView implements Callback {
	public static final String STATE_FILE = "asketch.png";
	public static final String OLD_STATE_FILE = "backup";

	public final class DrawThread extends Thread {
		private boolean mRun = true;
		private boolean mPause = false;

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
			controller.setCanvas(drawArea);

			try {
				FileInputStream fis = getContext().openFileInput(STATE_FILE);
				Bitmap savedBitmap = BitmapFactory.decodeStream(fis);
				if (savedBitmap != null) {
					drawArea.drawBitmap(savedBitmap, 0, 0, null);
				}
			} catch (FileNotFoundException e) {
			}

			while (mRun) {
				while (mPause) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				Canvas canvas = null;
				try {
					canvas = surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						controller.draw(drawArea);
						canvas.drawBitmap(bitmap, 0, 0, null);
					}
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		public void stopDrawing() {
			mRun = false;
		}

		public void pauseDrawing() {
			mPause = true;
		}

		public void resumeDrawing() {
			mPause = false;
		}
	}

	private DrawThread drawThread;
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
		drawThread = new DrawThread();
		drawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		drawThread.stopDrawing();
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
		controller.clear();
	}

	public void saveState() {
		try {
			FileOutputStream fos = getContext().openFileOutput(STATE_FILE,
					Context.MODE_WORLD_READABLE);
			bitmap.compress(CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void saveBitmap(String fileName) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(fileName);
		bitmap.compress(CompressFormat.PNG, 100, fos);
	}

	public DrawThread getThread() {
		return drawThread;
	}
}
