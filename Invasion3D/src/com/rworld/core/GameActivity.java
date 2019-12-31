package com.rworld.core;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import com.rworld.core.graphics.GraphicEngineFactory;
import com.rworld.core.graphics.GraphicEngine;

public abstract class GameActivity extends Activity implements OnTouchListener, SensorEventListener {
	
	public static final int GRAPHIC_LOW = 0;
    public static final int GRAPHIC_MED = 1;
    public static final int GRAPHIC_HIGH = 2;

	public static int GraphicLevel = GameActivity.GRAPHIC_MED;
    public static boolean IsMusicOn = true;
    public static boolean IsSoundOn = true;
    
	public GraphicEngine graphicEngine = null;
	public float lastFrameDeltaTime = 0.0f;
	public long lastFrameStartTime = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		
		SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0) {
			Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		}
		
		graphicEngine = GraphicEngineFactory.createInstance(this);
		_mainLoop = new GameMainLoop(this);
		_mainLoop.start();
	}
	
	
	@Override
	protected void onPause() {
		_mainLoop.onPause();
		graphicEngine.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		graphicEngine.onResume();
		_mainLoop.onResume();
	}
	
	protected abstract void loadStates();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_BACK:
	    	_isBackTouched = true; 
	        return true;
	    case KeyEvent.KEYCODE_MENU:
	    	_isMenuTouched = true; 
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_BACK:
	    	_isBackTouched = false; 
	        return true;
	    case KeyEvent.KEYCODE_MENU:
	    	_isMenuTouched = false; 
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			_isScreenTouched = true;
			graphicEngine.devicePointToVirtualPoint(event.getX(), event.getY(), _touchCoord);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			_isScreenTouched = false;
		}
		return true;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		System.arraycopy(event.values, 0, _acceleration, 0, 3);
	}

	public final boolean isMenuTouched() {
		return _isMenuTouched;
	}
	
	public final boolean isBackTouched() {
		return _isBackTouched;
	}

	public final boolean isTouched() {
		return _isScreenTouched;
	}
	
	public final int getTouchX() {
		return _touchCoord[0];
	}

	public final int getTouchY() {
		return _touchCoord[1];
	}

	public final float getAccelerationX() {
		return _acceleration[0];
	}

	public final float getAccelerationY() {
		return _acceleration[1];
	}

	public final float getAccelerationZ() {
		return _acceleration[2];
	}
	
	public void setInitialState(GameState initialState) {
		_mainLoop.initialState = initialState;
	}

	public void showCustomDialog(int dialogId) {
		_dialogResult = null;
		Message msg = _showDialogHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("showDialogId", dialogId);
        msg.setData(b);
        _showDialogHandler.sendMessage(msg);
	}
	
	public void setCustomDialofResult(Object result) {
		synchronized(_lockDialogResult) {
			_dialogResult = result;
			_lockDialogResult.notify();
		}
	}
	
	public Object getCustomDialogResult() {
		try {
			synchronized(_lockDialogResult) {
				while(_dialogResult == null) {
					_lockDialogResult.wait();
				}
			}
		} catch (InterruptedException ignore) {
		}
		return _dialogResult;
	}

	public void resetTime() {
		lastFrameDeltaTime = 0.0f;
		lastFrameStartTime = System.nanoTime();
	}
	
	public void recordTime() {
		final long currentFrameStartTime = System.nanoTime();
		lastFrameDeltaTime = (currentFrameStartTime - lastFrameStartTime) * 0.000000001f;
		lastFrameStartTime = currentFrameStartTime;
	}
	
	private Object _lockDialogResult = new Object();
	private Object _dialogResult = null;
	private GameMainLoop _mainLoop = null;
	private boolean _isMenuTouched = false;
	private boolean _isBackTouched = false;
	private boolean _isScreenTouched = false;
	private int[] _touchCoord = {0, 0};
	private float[] _acceleration = {0.0f, 0.0f, 0.0f};
	private final Handler _showDialogHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
        	showDialog(msg.getData().getInt("showDialogId"));
        }
    };
}
