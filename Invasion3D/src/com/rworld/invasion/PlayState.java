package com.rworld.invasion;

import javax.microedition.khronos.opengles.GL10;

import com.rworld.core.GameActivity;
import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.states.SwitchState;

public class PlayState extends SwitchState {
	
	public static final int CONTROL_TILT = 0;
    public static final int CONTROL_TOUCH = 1;
	
	public static final int STATE_PLAY = 0;
	public static final int STATE_PAUSE = 1;
	public static final int STATE_NEXTLEVEL = 2;
	public static final int STATE_GAMEOVER = 3;
	
	public boolean isAutoShootOn = false;
    public int typeofControl = PlayState.CONTROL_TILT;
	public int score = 0;
	public int level = 0;
	public float progression = 0.0f;
	public boolean isNewGame = true;
	
	public Simulation simulation;
	public Renderer renderer;
	public Benchmark benchmark;
	public int state;
	
	@Override
	public void init(GameActivity activity, GraphicEngine ge) {
		super.init(activity, ge);
		
		if(isNewGame) {
			score = 0;
			level = 0;
		}
		progression = 0.0f;
		isNewGame = false;
		
		simulation = new Simulation(this);
		renderer = new Renderer(this);
		benchmark = new Benchmark();
		state = PlayState.STATE_PLAY;
	}
	
	@Override
	public void dispose(GraphicEngine ge) {
		super.dispose(ge);
		benchmark = null;
		renderer = null;
		simulation = null;
	}
	
	@Override
	public void resume() {
		super.resume();
		breakCase(1);
	}
	
	@Override
	public void update() {
		switch(state) {
		case PlayState.STATE_PLAY:
			if(activity.isBackTouched()) {
				breakCase(MainActivity.STATE_ALL_TO_MENU);
			}
			else if(activity.isMenuTouched()) {
				musicManager.pauseMusic();
				_wait = 1.0f;
				state = PlayState.STATE_PAUSE;
			}
			else if(progression >= 1.0f) {
				_wait = 5.0f;
				state = PlayState.STATE_NEXTLEVEL;
			}
			else if(simulation.heroShip.life <= 0.0f) {
				_wait = 1.0f;
				state = PlayState.STATE_GAMEOVER;
			}
			else {
				simulation.heroShip.canCollide = true;
				simulation.heroShip.roll = 4.0f * activity.getAccelerationY();
				simulation.heroShip.fire(activity.isTouched() || isAutoShootOn);
				simulation.update();
			}
			break;
		case PlayState.STATE_PAUSE:
			_wait = Math.max(_wait - activity.lastFrameDeltaTime, 0.0f);
			if((_wait == 0.0f) && activity.isTouched()) {
				musicManager.playMusic();
				state = PlayState.STATE_PLAY;
			}
			break;
		case PlayState.STATE_NEXTLEVEL:
			_wait = Math.max(_wait - activity.lastFrameDeltaTime, 0.0f); 
			if(_wait == 0.0f) {
				level++;
				progression = 0.0f;
				breakCase(MainActivity.STATE_PLAY_TO_NEXTLEVEL);
			}
			else {
				simulation.heroShip.canCollide = false;
				simulation.heroShip.roll = 0.0f;
				simulation.heroShip.throttle = 5.0f;
				simulation.update();
			}
			break;
		case PlayState.STATE_GAMEOVER:
			_wait = Math.max(_wait - activity.lastFrameDeltaTime, 0.0f); 
			if((_wait == 0.0f) && activity.isTouched()) {
				isNewGame = true;
				breakCase(MainActivity.STATE_PLAY_TO_HIGHSCORE);
			}
			else {
				simulation.update();
			}
			break;
		}
		benchmark.update(this);
	}

	@Override
	public void render(GraphicEngine ge) {
		ge.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		ge.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		ge.gl.glLoadIdentity();
		ge.gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		ge.gl.glTranslatef(0.0f, -15.0f, 3.5f);
		ge.gl.glRotatef(-40.0f, 1.0f, 0.0f, 0.0f);
		
		renderer.renderBackground(ge);
		renderer.renderForeground(ge);
		ge.enterView2D();
		renderer.renderHUD(ge);
		ge.leaveView2D();
		
		benchmark.render(ge);
	}

	@Override
	public void loadAssets(GraphicEngine ge) {
		renderer.loadAssets(ge);
		simulation.createNewWave();
	}
	
	@Override
	public void unloadAssets(GraphicEngine ge) {
		renderer.unloadAssets(ge);
	}

	private float _wait;
}
