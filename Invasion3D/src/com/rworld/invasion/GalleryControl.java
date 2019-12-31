package com.rworld.invasion;

import java.io.IOException;

import android.util.Log;

import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.GameEntity;
import com.rworld.core.graphics.IMesh;
import com.rworld.core.graphics.MeshLoader;
import com.rworld.core.graphics.Surface;
import com.rworld.core.graphics.Texture;
import com.rworld.core.states.MenuState;
import com.rworld.core.states.menu.MenuControl;
import com.rworld.core.states.menu.controls.MenuButtonControl;
import com.rworld.core.states.menu.controls.MenuLabelControl;
import com.rworld.core.states.menu.controls.MenuPanelControl;
import com.rworld.core.states.menu.controls.OnTouchListener;
import com.rworld.core.states.menu.effects.BlinkIdleEffect;
import com.rworld.core.utilities.Utility;
import com.rworld.invasion.database.EntityRow;

public class GalleryControl extends MenuPanelControl {
	
	public MenuLabelControl labelControl;
	
	@Override
	public void init(MenuState menuState, MenuControl parent) {
		super.init(menuState, parent);
		autoPack = false;
		
		_prevButton.layout.set(0, (layout.height - 110) / 2, 60, 110);
		_prevButton.backgroundSurface = new Surface(130, 198, 20, 38);
		_prevButton.idleEffect = new BlinkIdleEffect(0.5f);
		_prevButton.text = null;
		_prevButton.visible =  false;
		_prevButton.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				if(_currentMeshEntity > 0) {
					_currentMeshEntity--;
					_isMeshEntityDirty = true;
				}
			}
    	};
    	addControl(_prevButton);
    	
    	_nextButton.layout.set(layout.width - 60, (layout.height - 110) / 2, 60, 110);
    	_nextButton.backgroundSurface = new Surface(152, 198, 20, 38);
    	_nextButton.idleEffect = new BlinkIdleEffect(0.5f);
    	_nextButton.text = null;
    	_nextButton.visible =  false;
    	_nextButton.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				if(_currentMeshEntity < (MainActivity.EntityTable.rows.length - 1)) {
					_currentMeshEntity++;
					_isMeshEntityDirty = true;
				}
			}
    	};
    	addControl(_nextButton);
    	
		_meshVisualEntity = new GameEntity(-3.0f, 0.0f, -10.0f); // dirty trick!
		_isMeshEntityDirty = true;
		_waitBeforeLoadMeshEntity = 0.2f;
	}

	@Override
	public void dispose(GraphicEngine ge) {
		_unloadMeshEntity(ge);
		super.dispose(ge);
	}
	
	@Override
	public void resume() {
		_isMeshEntityDirty = true;
		_waitBeforeLoadMeshEntity = 1.0f;
	}
	
	@Override
	public void update() {
		super.update();
		_prevButton.visible = _prevButton.visible && (_currentMeshEntity > 0);
		_nextButton.visible = _nextButton.visible && (_currentMeshEntity < (MainActivity.EntityTable.rows.length - 1));
		float t = menuState.time;
		_meshVisualEntity.rotationX = _meshVisualEntity.rotationX + 50.0f * t;
		_meshVisualEntity.rotationY = _meshVisualEntity.rotationY + 50.0f * t;
		_waitBeforeLoadMeshEntity = Math.max(_waitBeforeLoadMeshEntity - t, 0.0f);
	}

	@Override
	public void render(GraphicEngine ge) {
		super.render(ge);
		if(_waitBeforeLoadMeshEntity == 0.0f) {
			_ensureMeshEntity(ge);
			ge.leaveView2D();
			ge.gl.glLoadIdentity();
			_meshEntity.draw(ge.gl, _meshVisualEntity);
			ge.enterView2D();
		}
	}
	
	private void _ensureMeshEntity(GraphicEngine ge) {
		if(_isMeshEntityDirty) {
			_unloadMeshEntity(ge);
			_loadMeshEntity(ge, MainActivity.EntityTable.rows[_currentMeshEntity]);
			_isMeshEntityDirty = false;
		}
	}
	
	private void _loadMeshEntity(GraphicEngine ge, EntityRow entityRow) {
		try {
			menuState.activity.showCustomDialog(MainActivity.PROGRESS_DIALOG);
			_meshEntity = ge.createMesh(new MeshLoader(menuState.activity.getAssets().open(entityRow.meshFilePath)));
			_meshEntity.setTexture(new Texture(ge.gl, menuState.activity.getAssets().open(entityRow.skinFilePath)));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load mesh '" + entityRow.meshFilePath + "'", e);
		} finally {
			if(labelControl != null) {
				labelControl.text = "Name:" + entityRow.name + "\nClass:" + entityRow.shipClass + "\nRace:" + entityRow.race;
			}
			menuState.activity.removeDialog(MainActivity.PROGRESS_DIALOG);
		}
	}
	
	private void _unloadMeshEntity(GraphicEngine ge) {
		_meshEntity = Utility.safeDispose(_meshEntity, ge.gl);
	}
	
	private MenuButtonControl _prevButton = new MenuButtonControl();
	private MenuButtonControl _nextButton = new MenuButtonControl();
	private GameEntity _meshVisualEntity = null;
	private int _currentMeshEntity = 0;
	private boolean _isMeshEntityDirty = true;
	private float _waitBeforeLoadMeshEntity = 0.0f;
	private IMesh _meshEntity = null;
}

