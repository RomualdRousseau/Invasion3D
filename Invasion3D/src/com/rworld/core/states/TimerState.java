package com.rworld.core.states;

import com.rworld.core.GameActivity;
import com.rworld.core.GameState;
import com.rworld.core.graphics.GraphicEngine;

public class TimerState extends GameState {

	public float timer = 1.0f;
	public boolean waitTouch = false;
	public GameState next;
	
	@Override
	public void init(GameActivity activity, GraphicEngine ge) {
		super.init(activity, ge);
		_wait = timer;
	}

	@Override
	public void update() {
		if(waitTouch) {
			_touched = activity.isTouched();
		}
		_wait = Math.max(_wait - activity.lastFrameDeltaTime, 0.0f); 
	}

	@Override
	public GameState getNextState() {
		return ((_wait == 0.0f) && (!waitTouch || _touched)) ? next : this;
	}
	
	private float _wait;
	private boolean _touched;
}
