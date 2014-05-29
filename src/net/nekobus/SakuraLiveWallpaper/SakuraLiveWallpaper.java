package net.nekobus.SakuraLiveWallpaper;

import java.util.ArrayList;
import java.util.Random;

import net.nekobus.SakuraLiveWallpaper.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class SakuraLiveWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
	//android.os.Debug.waitForDebugger();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable e) {
				
				StackTraceElement[] elements = e.getStackTrace();
				for (StackTraceElement element : elements) {
					Log.e("Error !!! ", element.toString());
				}
			}
		});
		return new SnowLiveWallpaperEngine();
	}
	
	class SnowLiveWallpaperEngine extends Engine implements Runnable {
		private boolean isRunning;// = true;
		private static final int NUM_OF_SAKURA = 40;
		private Bitmap bgImage;
		private int screenW;
		private int screenH;
		private int xPixelOffset;
		private boolean isTouched;
		private ArrayList<Sakura> sakuraList = new ArrayList<Sakura>();
		Random random = new Random();
		Bitmap[] sakuras;
		Thread thread;
		int mTRight, mTBottom, mTLeft, mTTop;
	
		
		public void run() {
			while(isRunning  ) {
				Canvas canvas = getSurfaceHolder().lockCanvas();

				if (canvas != null) {
					
					// set background
					Rect src = new Rect(0, 0, bgImage.getWidth(),
							bgImage.getHeight());
					Rect dst = new Rect(0, 0, screenW, screenH);
					canvas.translate(xPixelOffset, 0);
					canvas.drawBitmap(bgImage, src, dst, null);

					// draw cherry blossom
					int count = 1;
					for (Sakura sakura : sakuraList) {
						//if it is closed to the touched area
						if(closeToTouched(sakura)){
							drawEffectedSakura(canvas,sakura);
						}else{
						if (count % 4 == 1) {
								canvas.drawBitmap(sakura.getImage(),
									sakura.getNextSX(), sakura.getNextSY(), null);
							}else{								
									canvas.drawBitmap(sakura.getImage(),
										sakura.getNextFallingX(),
										sakura.getNextFallingY(), null);
							}
						}
						count++;
					}
				}

				getSurfaceHolder().unlockCanvasAndPost(canvas);
				 isTouched = false;
			}
		}
		
		private void drawEffectedSakura(Canvas canvas, Sakura sakura) {
			 Paint paint = new Paint();
			 int radius = 4; 
			 paint.setColor(0xFFFFCCCC); 
			 paint.setMaskFilter(new BlurMaskFilter(radius, Blur.SOLID));  
			 int[] offsetXY = { 0, 0 };  
		
			Bitmap bmpUp =sakura.getImage();
			Bitmap bmpGlow = bmpUp.extractAlpha(paint, offsetXY);   
			Bitmap bmpDown = Bitmap.createBitmap(bmpUp.getWidth(), bmpUp.getHeight(), Bitmap.Config.ARGB_8888);  
			canvas.drawBitmap(bmpDown, sakura.getBlownX(), sakura.getBlownY(), paint); 
			canvas.drawBitmap(bmpGlow, sakura.getX(), sakura.getY(), paint); 
		    
		}

		public boolean closeToTouched(Sakura sakura){
			if (isTouched == true){
				if (sakura.getX() <= mTRight
					&& (mTLeft <= sakura.getX())
					&& ((sakura.getY() <= mTTop) && (mTBottom <= sakura
							.getY()))) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			//Load images from resources
			if (thread == null) {
			bgImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.sakura_pink);
			Bitmap sakura_a = BitmapFactory.decodeResource(getResources(),
					R.drawable.sakura_a);
			Bitmap sakura_b = BitmapFactory.decodeResource(getResources(),
					R.drawable.sakura_b);
			Bitmap sakura_c = BitmapFactory.decodeResource(getResources(),
					R.drawable.sakura_c);
			Bitmap sakura_d = BitmapFactory.decodeResource(getResources(),
					R.drawable.sakura_d);

			WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			screenW = getWallpaperDesiredMinimumWidth();
			screenH = getWallpaperDesiredMinimumHeight();
			if (screenW <= 0) {
				screenW = windowManager.getDefaultDisplay().getWidth();
			}
			if (screenH <= 0) {
				screenH = windowManager.getDefaultDisplay().getHeight();
			}
			sakuras = new Bitmap[] { sakura_a, sakura_b, sakura_c, sakura_d };
			for (int i = 0; i < NUM_OF_SAKURA; i++) {
				int pos = i % 4;

				Sakura aSakura = new Sakura(sakuras[pos], screenW, screenH);
				sakuraList.add(aSakura);
			}
			
			
			//Start live wallpaper
			isRunning = true;
			thread = new Thread(this);
			thread.start();
			}
			super.onSurfaceCreated(holder);
			
		}
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {

			if (thread != null) {

				if (!Thread.currentThread().isInterrupted()) {
					thread.interrupt();
					
				}
			}
			isRunning = false;
			super.onSurfaceDestroyed(holder);
		}
		
		@Override
		public void onVisibilityChanged(boolean visible) {
			// To save battery, run thread only when the wallpaper is visible
			if (thread != null) {
				if (visible) {
					if (isRunning == false) {
						isRunning = true;
						thread = new Thread(this);
						thread.start();
					}
				} else {
					if (!Thread.currentThread().isInterrupted()) {
						thread.interrupt();
						isRunning = false;
					}
				}
			}
		}
		
		@Override
		public void onTouchEvent(MotionEvent event) {

		     isTouched = true;
			int mTouchX = (int) event.getX() - xPixelOffset;
			int mTouchY = (int) event.getY();
			int mApprox = 350;

			if ((mTouchX + mApprox) > screenW) {
				mTRight = screenW;
			} else {
				mTRight = mTouchX + mApprox ;
			}

			if ((mTouchY + mApprox) > screenH) {
				mTTop = screenH;
			} else {
				mTTop = mTouchY + mApprox;
			}

			if ((mTouchX - mApprox ) < 0) {
				mTLeft = 0;
			} else {
				mTLeft = mTouchX - mApprox ;
			}

			if ((mTouchY - mApprox) < 0) {
				mTBottom = 0;
			} else {
				mTBottom = mTouchY - mApprox;
			}
			super.onTouchEvent(event);
		}
		
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			this.xPixelOffset = xPixelOffset;
		}
	}
}
