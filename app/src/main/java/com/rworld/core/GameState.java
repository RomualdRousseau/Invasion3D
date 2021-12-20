package com.rworld.core;

import android.util.Log;

import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.graphics.Surface;
import com.rworld.core.graphics.Texture;
import com.rworld.core.states.ImageProperty;
import com.rworld.core.states.MusicProperty;
import com.rworld.core.utilities.Utility;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

public abstract class GameState {

    public static final GameState FinalState = null;

    public final MusicProperty music = new MusicProperty();
    public final ImageProperty background = new ImageProperty();
    public OnLoadGameStateSoundListener onLoadGameStateSoundListener;

    public GameActivity activity = null;
    public GameMusicManager musicManager = null;

    public boolean isHighQuality = true;
    public boolean isMusicOn = true;
    public boolean isSoundOn = true;

    public void init(GameActivity activity, GraphicEngine ge) {
        this.activity = activity;
        this.musicManager = new GameMusicManager(activity);
        _background = new Surface(0, 0, background.width, background.height);
        _areSoundsDirty = true;
        _areAssetsDirty = true;
    }

    public void dispose(GraphicEngine ge) {
        unloadAssets(ge);
        _background = Utility.safeDispose(_background, ge.gl);
        musicManager = Utility.safeDispose(musicManager);
    }

    public void pause() {
        musicManager = Utility.safeDispose(musicManager);
    }

    public void resume() {
        _areAssetsDirty = true;
        _areSoundsDirty = true;
        if ((activity != null) && (musicManager == null)) {
            musicManager = new GameMusicManager(activity);
        }
    }

    public void playSound(int soundId) {
        if (GameActivity.IsSoundOn) {
            musicManager.playSound(soundId);
        }
    }

    public void ensureAssets(GraphicEngine ge) {
        if (_areAssetsDirty) {
            unloadAssets(ge);
            loadAssets(ge);
            _areAssetsDirty = false;
        }
    }

    public void ensureSounds() {
        if (_areSoundsDirty) {
            loadSounds();
            _areSoundsDirty = false;
        }
    }

    public void loadSounds() {
        try {
            if (GameActivity.IsMusicOn && (music.filePath != null)) {
                musicManager.loadMusic(activity.getAssets().openFd(music.filePath));
            }
        } catch (IOException e) {
            Log.e("com.rworld", "couldn't load music '" + music.filePath + "'", e);
        }
        if (GameActivity.IsSoundOn && (onLoadGameStateSoundListener != null)) {
            onLoadGameStateSoundListener.onLoadSounds(this);
        }
    }

    public void loadAssets(GraphicEngine ge) {
        try {
            if (background.filePath != null) {
                _background.setTexture(new Texture(ge.gl, activity.getAssets().open(background.filePath)));
            }
        } catch (IOException e) {
            Log.e("com.rworld", "couldn't load texture '" + background.filePath + "'", e);
        }
    }

    public void unloadAssets(GraphicEngine ge) {
    }

    public abstract void update();

    public void render(GraphicEngine ge) {
        ge.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        ge.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ge.enterView2D();
        ge.drawSurface2D(_background, 0, 0, GraphicEngine.WIDTH, GraphicEngine.HEIGHT);
        ge.leaveView2D();
    }

    public abstract GameState getNextState();

    protected Surface _background = null;
    private boolean _areAssetsDirty = true;
    private boolean _areSoundsDirty = true;
}
