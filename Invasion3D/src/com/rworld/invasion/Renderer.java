package com.rworld.invasion;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.rworld.core.GameActivity;
import com.rworld.core.graphics.Font;
import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.graphics.IMesh;
import com.rworld.core.graphics.IParticleGenerator;
import com.rworld.core.graphics.MeshLoader;
import com.rworld.core.graphics.Texture;
import com.rworld.core.utilities.Utility;
import com.rworld.invasion.database.EntityRow;
/*
import com.rworld.invasion.simulation.Bonus;
*/

public class Renderer implements SimulationListener {
	
	public Renderer(PlayState playState) {
		_playState = playState;
		_playState.simulation.simulationListener = this;
	}
	
	public void loadAssets(GraphicEngine ge) {
		try {
			_font = new Font(16, 16);
			_font.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("fonts/font16x16.png")));
		} catch (IOException e) {
			Log.e("com.rworld", "couldn't load texture '" + "fonts/font16x16.png" + "'", e);
		}
		try {
			_smallExplosions = ge.createParticleGenerator(20, 40.0f);
			_smallExplosions.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("meshes/small_explosion.png")));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load texture 'meshes/small_explosion.png'", e);
			throw new RuntimeException("couldn't load texture 'meshes/small_explosion.png'");
		}
		try {
			_bigExplosions = ge.createParticleGenerator(100, 20.0f);
			_bigExplosions.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("meshes/big_explosion.png")));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load texture 'meshes/big_explosion.png'", e);
			throw new RuntimeException("couldn't load texture 'meshes/big_explosion.png'");
		}
		try {
			_heroShip = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open(MainActivity.EntityTable.rows[0].meshFilePath)));
			_heroShip.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open(MainActivity.EntityTable.rows[0].skinFilePath)));
			_heroBullet = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open(MainActivity.EntityTable.rows[0].bulletFilePath)), 2.0f, 2.0f, 2.0f);
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load mesh '" + MainActivity.EntityTable.rows[0].name + "'", e);
			throw new RuntimeException("couldn't load mesh '" + MainActivity.EntityTable.rows[0].name + "'");
		}
		/*
		try {
			_bonusShield = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open("meshes/bonus_shield.obj")));
			_bonusShield.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("meshes/bonus_shield_skin.png")));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load mesh 'Bonus Shield'", e);
			throw new RuntimeException("couldn't load mesh 'Bonus Shield'");
		}
		try {
			_bonusEnergy = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open("meshes/bonus_energy.obj")));
			_bonusEnergy.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("meshes/bonus_energy_skin.png")));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load mesh 'Bonus Energy'", e);
			throw new RuntimeException("couldn't load mesh 'Bonus Energy'");
		}
		try {
			_bonusDoubleFire = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open("meshes/bonus_double_fire.obj")));
			_bonusDoubleFire.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("meshes/bonus_double_fire_skin.png")));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load mesh 'Double Fire'", e);
			throw new RuntimeException("couldn't load mesh 'Double Fire'");
		}
		try {
			_bonusTripleFire = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open("meshes/bonus_triple_fire.obj")));
			_bonusTripleFire.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("meshes/bonus_triple_fire_skin.png")));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load mesh 'Bonus Triple Fire'", e);
			throw new RuntimeException("couldn't load mesh 'Triple Fire'");
		}
		try {
			_bonusCrossFire = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open("meshes/bonus_cross_fire.obj")));
			_bonusCrossFire.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open("meshes/bonus_cross_fire_skin.png")));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load mesh 'Bonus Cross Fire'", e);
			throw new RuntimeException("couldn't load mesh 'Bonus Cross Fire'");
		}
		*/
		for(EntityRow entityRow : _playState.simulation.enemyTableForLevel) {
			try {
				_enemyShip = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open(entityRow.meshFilePath)));
				_enemyShip.setTexture(new Texture(ge.gl, _playState.activity.getAssets().open(entityRow.skinFilePath)));
				_enemyBullet = ge.createMesh(new MeshLoader(_playState.activity.getAssets().open(entityRow.bulletFilePath)), 2.0f, 2.0f, 2.0f);
				_enemyShips.add(_enemyShip);
				_enemyBullets.add(_enemyBullet);
			} catch (IOException e) {
				Log.e("Invasion3D", "couldn't load mesh '" + entityRow.name + "'", e);
				throw new RuntimeException("couldn't load mesh '" + entityRow.name + "'");
			}
		}
	}
	
	public void unloadAssets(GraphicEngine ge) {	
		for(IMesh enemyBullet : _enemyBullets) {
			enemyBullet.dispose(ge.gl);
		}
		_enemyBullets.clear();
		for(IMesh enemyShip : _enemyShips) {
			enemyShip.dispose(ge.gl);
		}
		_enemyShips.clear();
		_heroBullet = Utility.safeDispose(_heroBullet, ge.gl);
		_heroShip = Utility.safeDispose(_heroShip, ge.gl);
		_bigExplosions = Utility.safeDispose(_bigExplosions, ge.gl);
		_smallExplosions = Utility.safeDispose(_smallExplosions, ge.gl);
		_font = Utility.safeDispose(_font, ge.gl);
	}
	
	public void renderBackground(GraphicEngine ge) {
		if(GameActivity.GraphicLevel == GameActivity.GRAPHIC_MED) {
			// display background
		}
		if(GameActivity.GraphicLevel == GameActivity.GRAPHIC_HIGH) {
			// display star field
		}
	}
	
	public void renderForeground(GraphicEngine ge) {
		if(_playState.simulation.heroShip.life > 0.0f) {
			_heroShip.draw(ge.gl, _playState.simulation.heroShip);
		}
		
		_enemyShip.draw(ge.gl, _playState.simulation.enemyShips);
		
		ge.gl.glDisable(GL10.GL_TEXTURE_2D);
		_heroBullet.draw(ge.gl, _playState.simulation.heroBullets);
		_enemyBullet.draw(ge.gl, _playState.simulation.enemyBullets);
		ge.gl.glEnable(GL10.GL_TEXTURE_2D);
		
		/*
		int s = _playState.simulation.bonuses.size();
		for(int i = 0; i < s; i++) {
			Bonus bonus = _playState.simulation.bonuses.get(i);
			if(bonus.type == Bonus.SHIELD) {
				_bonusShield.draw(ge.gl, bonus);
			}
			else if(bonus.type == Bonus.ENERGY) {
				_bonusEnergy.draw(ge.gl, bonus);
			}
			else if(bonus.type == Bonus.DOUBLE_FIRE) {
				_bonusDoubleFire.draw(ge.gl, bonus);
			}
			else if(bonus.type == Bonus.TRIPLE_FIRE) {
				_bonusTripleFire.draw(ge.gl, bonus);
			}
			else if(bonus.type == Bonus.CROSS_FIRE) {
				_bonusCrossFire.draw(ge.gl, bonus);
			}
		}
		*/
		
		ge.gl.glDisable(GL10.GL_DEPTH_TEST);
		ge.gl.glEnable(GL10.GL_BLEND);
		_smallExplosions.draw(ge.gl, _playState.simulation.smallExplosions);
		_bigExplosions.draw(ge.gl, _playState.simulation.bigExplosions);
		ge.gl.glDisable(GL10.GL_BLEND);
		ge.gl.glEnable(GL10.GL_DEPTH_TEST);
	}
	
	public void renderHUD(GraphicEngine ge) {
		ge.gl.glEnable(GL10.GL_BLEND);
		ge.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		_hudLabel.setLength(0);
		_hudLabel.append("Score:").append(_playState.score);
		ge.drawText2D(_font, 0, 0, 1.0f, _hudLabel.toString());
		
		_hudLabel.setLength(0);
		_hudLabel.append("Level:").append(_playState.level + 1).append("-").append((int)(_playState.progression * 100.0f)).append("%");
		ge.drawText2D(_font, GraphicEngine.WIDTH - _hudLabel.length() * _font.getWidth() * 1, 0, 1.0f, _hudLabel.toString());
		
		_hudLabel.setLength(0);
		for(int i = 0; i < (int)_playState.simulation.heroShip.life; i++) {
			_hudLabel.append("/");
		}
		ge.drawText2D(_font, 0, GraphicEngine.HEIGHT - _font.getHeight() * 2, 2.0f, _hudLabel.toString());
		
		switch(_playState.state) {
		case PlayState.STATE_PAUSE:
			ge.drawText2D(_font, (GraphicEngine.WIDTH - 5 * _font.getWidth() * 2) / 2, (GraphicEngine.HEIGHT - _font.getHeight() * 2) / 2, 2.0f, "Pause");
			break;
		case PlayState.STATE_GAMEOVER:
			ge.drawText2D(_font, (GraphicEngine.WIDTH - 9 * _font.getWidth() * 2) / 2, (GraphicEngine.HEIGHT - _font.getHeight() * 2) / 2, 2.0f, "Game Over");
			break;
		}
		
		ge.gl.glDisable(GL10.GL_BLEND);
	}
	
	public void onCreateNewWave(int enemyLastIndex) {
		_enemyShip = _enemyShips.get(enemyLastIndex);
		_enemyBullet = _enemyBullets.get(enemyLastIndex);
	}
	
	private final PlayState _playState;
	private Font _font;
	private IParticleGenerator _smallExplosions;
	private IParticleGenerator _bigExplosions;
	private IMesh _heroShip;
	private IMesh _heroBullet;
/*
	private IMesh _bonusShield;
	private IMesh _bonusEnergy;
	private IMesh _bonusDoubleFire;
	private IMesh _bonusTripleFire;
	private IMesh _bonusCrossFire;
*/
	private IMesh _enemyShip;
	private IMesh _enemyBullet;
	private final ArrayList<IMesh> _enemyShips = new ArrayList<IMesh>();
	private final ArrayList<IMesh> _enemyBullets = new ArrayList<IMesh>();
	private final StringBuilder _hudLabel = new StringBuilder();
}
