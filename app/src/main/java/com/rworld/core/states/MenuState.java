package com.rworld.core.states;

import android.util.Log;

import com.rworld.core.GameActivity;
import com.rworld.core.graphics.Font;
import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.graphics.Texture;
import com.rworld.core.states.menu.MenuControl;
import com.rworld.core.states.menu.OnLoadMenuStateListener;
import com.rworld.core.utilities.Utility;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class MenuState extends SwitchState {

    public final static int ANIM_ENTRANCE = 0;
    public final static int ANIM_IDLE = 1;
    public final static int ANIM_EXIT = 2;
    public final static int ANIM_DISPOSE = 3;

    public final ImageProperty font = new ImageProperty();
    public final ImageProperty decoration = new ImageProperty();
    public OnLoadMenuStateListener onLoadMenuStateListener;

    public int animationState = MenuState.ANIM_ENTRANCE;
    public Font internalFont;

    public float time;

    public void addControl(MenuControl control) {
        control.init(this, null);
        _controls.add(control);
    }

    @Override
    public void init(GameActivity activity, GraphicEngine ge) {
        super.init(activity, ge);
        animationState = MenuState.ANIM_ENTRANCE;
        internalFont = new Font(font.width, font.height);
        if (onLoadMenuStateListener != null) {
            onLoadMenuStateListener.onLoadControls(this);
        }
    }

    @Override
    public void dispose(GraphicEngine ge) {
        for (MenuControl control : _controls) {
            control.dispose(ge);
        }
        _controls.removeAll(_controls);
        internalFont = Utility.safeDispose(internalFont, ge.gl);
        super.dispose(ge);
    }

    @Override
    public void pause() {
        for (MenuControl control : _controls) {
            control.pause();
        }
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
        for (MenuControl control : _controls) {
            control.resume();
        }
    }

    @Override
    public int condition() {
        return (animationState == MenuState.ANIM_DISPOSE) ? super.condition() : -1;
    }

    @Override
    public void breakCase(int command) {
        super.breakCase(command);
        animationState = MenuState.ANIM_EXIT;
    }

    @Override
    public void update() {
        boolean allFinished;
        time = activity.lastFrameDeltaTime;
        switch (animationState) {
            case MenuState.ANIM_ENTRANCE:
                allFinished = true;
                for (MenuControl control : _controls) {
                    allFinished &= !control.inEffect.update(control);
                }
                if (allFinished) {
                    animationState = MenuState.ANIM_IDLE;
                }
                break;
            case MenuState.ANIM_IDLE:
                for (MenuControl control : _controls) {
                    control.idleEffect.update(control);
                    control.update();
                }
                break;
            case MenuState.ANIM_EXIT:
                allFinished = true;
                for (MenuControl control : _controls) {
                    allFinished &= !control.outEffect.update(control);
                }
                if (allFinished) {
                    animationState = MenuState.ANIM_DISPOSE;
                }
                break;
        }
    }

    @Override
    public void render(GraphicEngine ge) {
        ge.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        ge.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        ge.enterView2D();
        ge.drawSurface2D(_background, 0, 0, 800, 480);
        ge.gl.glEnable(GL10.GL_BLEND);
        for (MenuControl control : _controls) {
            if (control.visible) {
                control.render(ge);
            }
        }
        ge.gl.glDisable(GL10.GL_BLEND);
        ge.leaveView2D();
    }

    @Override
    public void loadAssets(GraphicEngine ge) {
        super.loadAssets(ge);
        try {
            internalFont.setTexture(new Texture(ge.gl, activity.getAssets().open(font.filePath)));
        } catch (IOException e) {
            Log.e("com.rworld", "couldn't load texture '" + font.filePath + "'", e);
        }
        try {
            Texture texture = new Texture(ge.gl, activity.getAssets().open(decoration.filePath));
            for (MenuControl control : _controls) {
                control.loadTextures(ge, texture);
            }
        } catch (IOException e) {
            Log.e("com.rworld", "couldn't load texture '" + decoration.filePath + "'", e);
        }
    }

    private ArrayList<MenuControl> _controls = new ArrayList<MenuControl>(5);
}
