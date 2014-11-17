package mn.student.letsgo.common;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

@SuppressWarnings("deprecation")
public class ShakeListener implements SensorListener {
	  private static final int FORCE_THRESHOLD = 350;
	  private static final int TIME_THRESHOLD = 100;
	  private static final int SHAKE_TIMEOUT = 300;
	  private static final int SHAKE_DURATION = 200;
	  private static final int SHAKE_COUNT = 5;

	  private SensorManager mSensorMgr;
	  private float mLastX=-1.0f;//, mLastY=-1.0f, mLastZ=-1.0f;
	  private long mLastTime;
	  private OnShakeListener mShakeListener;
	  private Context mContext;
	  private int mShakeCount = 3;
	  private long mLastShake;
	  private long mLastForce;

	  public interface OnShakeListener{
		  public void onShake();
	  }

	  public ShakeListener(Context context) { 
		  mContext = context;
		  resume();
	  }

	  public void setOnShakeListener(OnShakeListener listener){
		  mShakeListener = listener;
	  }

	  public void resume() {
		  mSensorMgr = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
		  if (mSensorMgr == null) {
			  throw new UnsupportedOperationException("Sensors not supported");
		  }
		  boolean supported = mSensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
		  if (!supported) {
			  mSensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
			  throw new UnsupportedOperationException("Accelerometer not supported");
		  }
	  }

	  public void pause() {
	    if (mSensorMgr != null) {
	      mSensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
	      mSensorMgr = null;
	    }
	  }

	  public void onAccuracyChanged(int sensor, int accuracy) { }

	  public void onSensorChanged(int sensor, float[] values) {
	    if (sensor != SensorManager.SENSOR_ACCELEROMETER) return;
	    long now = System.currentTimeMillis();

	    if ((now - mLastForce) > SHAKE_TIMEOUT) {
	      mShakeCount = 0;
	    }

	    if ((now - mLastTime) > TIME_THRESHOLD) {
	      long diff = now - mLastTime;
	      //float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 5000;
	      float speed = Math.abs(values[SensorManager.DATA_X]  - mLastX) / diff * 2500;
	      if (speed > FORCE_THRESHOLD) {
	        if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
	          mLastShake = now;
	          mShakeCount = 0;
	          if (mShakeListener != null) { 
	            mShakeListener.onShake(); 
	          }
	        }
	        mLastForce = now;
	      }
	      mLastTime = now;
	      mLastX = values[SensorManager.DATA_X];
	      //mLastY = values[SensorManager.DATA_Y];
	      //mLastZ = values[SensorManager.DATA_Z];
	    }
	  }

	}