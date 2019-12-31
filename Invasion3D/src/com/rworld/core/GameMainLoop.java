package com.rworld.core;

import com.rworld.core.graphics.GraphicEngine;

public class GameMainLoop extends Thread {
	
	public GameState initialState = GameState.FinalState;
	
	public GameMainLoop(GameActivity activity) {
        super("GameMainLoop");
        _activity = activity;
    }
	
	@Override
	public void run() {
    	_running = true;
    	_activity.loadStates();
    	while(_running) {
    		runOnce();
		}
    	_activity.graphicEngine.callGraphicCommand(GraphicEngine.COMMAND_FINISH, null);
    	_activity.finish();
    }

	public void onPause() {
		// suspend();
		if(_currentState != null) {
			_currentState.pause();
		}
	}
	
	public void onResume() {
		_activity.resetTime();
		if(_currentState != null) {
			_currentState.resume();
		}
		// resume();
	}
	
	public void runOnce() {
		GameState nextState = (_currentState != null) ? _currentState.getNextState() : initialState;
		if (nextState != _currentState) {
			if(_currentState != null) {
				_activity.graphicEngine.callGraphicCommand(GraphicEngine.COMMAND_DISPOSE, _currentState);
				_currentState = null;
				System.gc();
			}
			_currentState = nextState;
			if(_currentState != null) {
				_activity.graphicEngine.callGraphicCommand(GraphicEngine.COMMAND_INIT, _currentState);
				_currentState.ensureSounds();
			}
			_activity.resetTime();
		}
		if(_currentState == GameState.FinalState) {
			_running = false;
		}
		else {
			_currentState.ensureSounds();
			_currentState.update();
			_activity.graphicEngine.callGraphicCommand(GraphicEngine.COMMAND_RENDER, _currentState);
		}
		_activity.recordTime();
    }

    private GameActivity _activity = null;
    private GameState _currentState = null;
    private boolean _running = false;
}
