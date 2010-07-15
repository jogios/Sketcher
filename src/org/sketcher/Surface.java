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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public final class Surface extends SurfaceView implements Callback {
	public static final String STATE_FILE = "asketch.png";
	public static final String OLD_STATE_FILE = "backup";

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

			try {
				FileInputStream fis = getContext().openFileInput(STATE_FILE);
				Bitmap savedBitmap = BitmapFactory.decodeStream(fis);
				if (savedBitmap != null) {
					drawArea.drawBitmap(savedBitmap, 0, 0, null);
				}
			} catch (FileNotFoundException e) {
				// ok, continue
			}
		}

		@Override
		public void run() {
			while (bitmap == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			SurfaceHolder surfaceHolder = getHolder();

			while (mRun) {
				while (mRun && mPause) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				Canvas canvas = null;
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

	public Surface(Context context) {
		super(context);

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

	public void saveState() {
		final Bitmap bitmap = getDrawThread().getBitmap();
		if (bitmap == null) {
			return;
		}
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
		getDrawThread().getBitmap().compress(CompressFormat.PNG, 100, fos);
	}

	public void setPaintColor(int color) {
		controller.setPaintColor(color);
	}

	public int getPaintColor() {
		return controller.getPaintColor();
	}

}
