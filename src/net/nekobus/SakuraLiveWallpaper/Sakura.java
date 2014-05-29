package net.nekobus.SakuraLiveWallpaper;

import java.util.Random;

import android.graphics.Bitmap;

public class Sakura {
	private float mX;
	private float mY;
	private Bitmap mImg;
	private int mScreenH;
	private int mScreenW;
	private int mMaxW;
	private float mFirstX;
	private Direction mDirection;
	private float mFallingSpeedX;
	
	private Random random = new Random();	
	private static int Y_RAN_SPEED = 4;
	private static float S_X_SPEED = 3.0f;
	private static float S_Y_SPEED = 3.0f;
	private float MIN_X_SPEED = 0.5f;
	private float MAX_Y_SPEED = 1.0f;
	enum Direction { Left, Right }

	Sakura(Bitmap pImage, int pWidth, int pHight) {
		mImg = pImage;
		mScreenH = pHight;
		mScreenW = pWidth;
		mMaxW = 300+random.nextInt(800);		
		mX = random.nextInt(mScreenH);
		mY = random.nextInt(mScreenW);
		mFirstX = mX + random.nextInt(4*2)-2;
		mDirection = Direction.Right;
		mFallingSpeedX = random.nextFloat() * (MAX_Y_SPEED - MIN_X_SPEED) + MIN_X_SPEED;
	}

	
	public float getNextSX() {
		mX = (int) (mX + S_X_SPEED );
		if (mX > mScreenW){
			mX = 0;
		}
		return mX;
	}

	public float getNextSY() {
		mY = mY + S_Y_SPEED;
		if (mY > mScreenH)
			mY = 0;
		return mY;
	}
	
	public float getX(){
		return mX;
	}

	public float getY(){
		return mY;
	}
	
	public Bitmap getImage() {
		return mImg;
	}

	public void setImage(Bitmap pImg) {
		mImg = pImg;
	}

	public float getNextFallingX() {
	
		int diff = (int)(mX - mFirstX);
		if((diff) == 0 ){
			mX = mX + mFallingSpeedX;
			mDirection = Direction.Right;
		}
		else if((diff) > 0 ){
			//right
			if(diff < mMaxW/2 ){
				if(mDirection == Direction.Right){
					mX = mX + mFallingSpeedX;
				}else{
					mX = mX - mFallingSpeedX;
				}
			}else{
				mX = mX - mFallingSpeedX;
				mDirection = Direction.Left;
			}
		}else{
			//left
			if(diff * -1 > mMaxW/2 ){
				mX = mX + mFallingSpeedX;
				mDirection = Direction.Left;
			}else{
				if(mDirection == Direction.Right){
					mX = mX + mFallingSpeedX;
				}else{
					mX = mX - mFallingSpeedX;
				}
			}
		}
			
		if(mX >= mScreenW){
			mDirection = Direction.Right;
			mX = 0;
		}else if(mX <= 0){
			mDirection = Direction.Left;
			mX = mScreenW;
		}
		
		return mX ;
	}

	public float getNextFallingY() {
		int vy =Y_RAN_SPEED;
		mY = vy + mY;
		if (mY > mScreenH)
		mY = 0;
		return mY ;
	}
	
	public float getBlownX() {
		mX = mX - (random.nextInt(20 *2)-20);
		if (mX > mScreenW)
			mX = 0;
		return mX ;
	}

	public float getBlownY() {
		int vy = (random.nextInt(20 *2)-20);
		mY =  mY - vy;
		if (mY > mScreenH)
		mY = 0;
		return mY ;
	}
}
