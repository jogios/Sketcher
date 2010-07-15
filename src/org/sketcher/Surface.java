package org.sketcher;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public final class Surface extends SurfaceView implements Callback {
	public static final String STATE_FILE = "asketch.png";

	public final class DrawThread extends Thread {
		private boolean mRun = true;
		private boolean mPause = false;

		private Bitmap bitmap;
		private Canvas drawArea;

		public Bitmap getBitmap() {
			return bitmap;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
			bitmap.eraseColor(Color.WHITE);
			drawArea = new Canvas(bitmap);
			controller.setCanvas(drawArea);

			Bitmap savedBitmap = context.getSavedBitmap();
			if (savedBitmap != null) {
				drawArea.drawBitmap(savedBitmap, 0, 0, null);
			}
		}

		@Override
		public void run() {
			waitForBitmap();

			SurfaceHolder surfaceHolder = getHolder();
			Canvas canvas = null;

			while (mRun) {
				while (mRun && mPause) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				try {
					canvas = surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						controller.draw();
						canvas.drawBitmap(bitmap, 0, 0, null);
					}
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

		private void waitForBitmap() {
			while (bitmap == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
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
	private Controller controller = new Controller();
	private Sketcher context;

	public Surface(Sketcher context) {
		super(context);

		this.context = context;

		getHolder().addCallback(this);
		setFocusable(true);
		setOnTouchListener(controller);
	}

	public void setStyle(Style style) {
		controller.setStyle(style);
	}

	public DrawThread getDrawThread() {
		if (drawThread == null) {
			drawThread = new DrawThread();
		}
		return drawThread;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		getDrawThread().setBitmap(
				Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		getDrawThread().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		getDrawThread().stopDrawing();
		while (true) {
			try {
				getDrawThread().join();
				break;
			} catch (InterruptedException e) {
			}
		}
		drawThread = null;
	}

	public void clearBitmap() {
		getDrawThread().getBitmap().eraseColor(Color.WHITE);
		controller.clear();
	}

	public void saveBitmap(String fileName) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(fileName);
		getDrawThread().getBitmap().compress(CompressFormat.PNG, 100, fos);
	}

	public void setPaintColor(int color) {
		controller.setPaintColor(color);
	}

	public int getPaintColor() {
		return controller.getPaintColor();
	}
}
