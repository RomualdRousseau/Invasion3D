package com.rworld.invasion;

import java.util.ArrayList;

import com.rworld.core.physics.Collision;
import com.rworld.core.physics.PhysicalEntityCollection;
import com.rworld.invasion.database.EntityRow;
import com.rworld.invasion.database.WaveRow;
import com.rworld.invasion.simulation.BigExplosion;
import com.rworld.invasion.simulation.BigExplosionFactory;
import com.rworld.invasion.simulation.Bonus;
import com.rworld.invasion.simulation.BonusFactory;
import com.rworld.invasion.simulation.EnemyBullet;
import com.rworld.invasion.simulation.EnemyBulletFactory;
import com.rworld.invasion.simulation.EnemyFactory;
import com.rworld.invasion.simulation.EnemyShip;
import com.rworld.invasion.simulation.HeroBullet;
import com.rworld.invasion.simulation.HeroBulletFactory;
import com.rworld.invasion.simulation.HeroShip;
import com.rworld.invasion.simulation.SmallExplosion;
import com.rworld.invasion.simulation.SmallExplosionFactory;

public class Simulation {
	
	public static final int MAX_BULLETS = 10;
	public static final int PARTICULES_PER_EXPLOSIONS = 5;
	public static final float PROGRESSION_STEP = 0.1f;
	
	public SimulationListener simulationListener = null;
	public final ArrayList<EntityRow> enemyTableForLevel = new ArrayList<EntityRow>();
	public int enemyLastIndex;
	
	public HeroShip heroShip = new HeroShip(MainActivity.EntityTable.rows[0], 0.0f , 0.0f, 0.0f);
	public PhysicalEntityCollection<SmallExplosion> smallExplosions = new PhysicalEntityCollection<SmallExplosion>(new SmallExplosionFactory());
	public PhysicalEntityCollection<BigExplosion> bigExplosions = new PhysicalEntityCollection<BigExplosion>(new BigExplosionFactory());
	public PhysicalEntityCollection<HeroBullet> heroBullets = new PhysicalEntityCollection<HeroBullet>(new HeroBulletFactory());
	public PhysicalEntityCollection<EnemyShip> enemyShips = new PhysicalEntityCollection<EnemyShip>(new EnemyFactory());
	public PhysicalEntityCollection<EnemyBullet> enemyBullets = new PhysicalEntityCollection<EnemyBullet>(new EnemyBulletFactory());
	public PhysicalEntityCollection<Bonus> bonuses = new PhysicalEntityCollection<Bonus>(new BonusFactory());

	public Simulation(PlayState playState) {
		_playState = playState;
		 enemyLastIndex = 0;
		 
		for(EntityRow entityRow : MainActivity.EntityTable.rows) {
			if((entityRow.level & (1 << _playState.level)) > 0) {
				enemyTableForLevel.add(entityRow);
			}
		}
	}
	
	public void createNewWave() {
		WaveRow waveRow = MainActivity.WaveTable.createRandom(_playState.level);
		for(int i = 0; i < waveRow.channels.length; i++) {
			enemyShips.spawn().init(enemyTableForLevel.get(enemyLastIndex), waveRow.channels[i]);
		}
		if(simulationListener != null) {
			simulationListener.onCreateNewWave(enemyLastIndex);
		}
		enemyLastIndex = (enemyLastIndex + 1) % enemyTableForLevel.size();
	}
	
	public void update() {
		if(heroShip.life > 0.0f) {
			if(enemyShips.isEmpty()) {
				_playState.progression = Math.min(_playState.progression + PROGRESSION_STEP, 1.0f);
				if(_playState.progression < 1.0f) {
					createNewWave();
				}
			}
			/*
			if((Math.random() > 0.9f) && bonuses.isEmpty()) {
				Bonus bonus = bonuses.spawn();
				bonus.init(5.0f);
			}
			*/
			if(heroShip.canonFired && (heroBullets.size() < Simulation.MAX_BULLETS)) {
				HeroBullet bullet = heroBullets.spawn();
				bullet.init(heroShip, 3.0f);
				//gameState.playSound(MainActivity.soundShotId);
			}
		
			for(int i = 0, n = enemyShips.size(); i < n; i++) {
				EnemyShip enemy = enemyShips.get(i);
				if(enemy.isCanonFired(_playState.level)) {
					EnemyBullet bullet = enemyBullets.spawn();
					bullet.init(enemy, 4.0f, heroShip);
					//gameState.playSound(MainActivity.soundShotId);
				}
				if(heroShip.canCollide && Collision.testSphere(enemy, heroShip)) {
					enemy.life = 0.0f;
					for(int j = 0; j< Simulation.PARTICULES_PER_EXPLOSIONS; j++) {
						bigExplosions.spawn().init(enemy, 3.0f);
					}
					heroShip.life = 0.0f;
					for(int j = 0; j < Simulation.PARTICULES_PER_EXPLOSIONS; j++) {
						bigExplosions.spawn().init(heroShip, 3.0f);
					}
					_playState.playSound(MainActivity.SoundBangId);
				}
			}
			
			for(int i = 0, n = heroBullets.size(); i < n; i++) {
				HeroBullet bullet = heroBullets.get(i);
				EnemyShip predator = (EnemyShip) Collision.testSphere(bullet, enemyShips);
				if(predator != null) {
					predator.life = predator.life - 1.0f;
					if(predator.life <= 0.0f) {
						for(int j = 0; j < Simulation.PARTICULES_PER_EXPLOSIONS; j++) {
							bigExplosions.spawn().init(predator, 3.0f);
						}
						_playState.score += 10;
					}
					else {
						smallExplosions.spawn().init(bullet, 0.2f);
					}
					_playState.playSound(MainActivity.SoundBangId);
					bullet.life = 0.0f;
				}
			}
		
			for(int i = 0, n = enemyBullets.size(); i < n; i++) {
				EnemyBullet bullet = enemyBullets.get(i);
				if(heroShip.canCollide && Collision.testSphere(bullet, heroShip)) {
					heroShip.life = heroShip.life - 1.0f;
					if(heroShip.life <= 0.0f) {
						for(int j = 0; j < Simulation.PARTICULES_PER_EXPLOSIONS; j++) {
							bigExplosions.spawn().init(heroShip, 3.0f);
						}
					}
					else {
						smallExplosions.spawn().init(bullet, 0.2f);
					}
					_playState.playSound(MainActivity.SoundBangId);
					bullet.life = 0.0f;
				}
			}

			heroShip.update(_playState);
		}
		
		enemyShips.updateAll(_playState);
		heroBullets.updateAll(_playState);
		enemyBullets.updateAll(_playState);
		smallExplosions.updateAll(_playState);
		bigExplosions.updateAll(_playState);
	}
	
	private final PlayState _playState;
}
