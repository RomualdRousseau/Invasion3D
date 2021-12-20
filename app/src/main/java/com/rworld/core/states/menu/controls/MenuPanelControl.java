package com.rworld.core.states.menu.controls;

import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.graphics.Texture;
import com.rworld.core.states.MenuState;
import com.rworld.core.states.menu.MenuControl;

import java.util.ArrayList;

public class MenuPanelControl extends MenuControl {

    public final static int DIRECTION_VERTICAL = 0;
    public final static int DIRECTION_HORIZONTAL = 1;

    public boolean autoPack = true;
    public int direction = MenuPanelControl.DIRECTION_VERTICAL;

    public void addControl(MenuControl newControl) {
        newControl.init(menuState, this);
        _controls.add(newControl);
        if (autoPack) {
            pack();
        }
    }

    public void pack() {
        if (direction == MenuPanelControl.DIRECTION_VERTICAL) {
            int y = 0;
            int h = layout.height / _controls.size();
            for (MenuControl control : _controls) {
                control.layout.x = 0;
                control.layout.y = y;
                control.layout.width = layout.width;
                control.layout.height = h;
                y += h;
            }
        } else /* if(direction == MenuPanelControl.DIRECTION_HORIZONTAL) */ {
            int x = 0;
            int w = layout.width / _controls.size();
            for (MenuControl control : _controls) {
                control.layout.x = x;
                control.layout.y = 0;
                control.layout.width = w;
                control.layout.height = layout.height;
                x += w;
            }
        }
    }

    @Override
    public void dispose(GraphicEngine ge) {
        for (MenuControl control : _controls) {
            control.dispose(ge);
        }
        super.dispose(ge);
    }

    @Override
    public void loadTextures(GraphicEngine ge, Texture texture) {
        super.loadTextures(ge, texture);
        for (MenuControl control : _controls) {
            control.loadTextures(ge, texture);
        }
    }

    @Override
    public void update() {
        super.update();
        switch (menuState.animationState) {
            case MenuState.ANIM_ENTRANCE:
                for (MenuControl control : _controls) {
                    control.inEffect.update(control);
                }
                break;
            case MenuState.ANIM_IDLE:
                for (MenuControl control : _controls) {
                    control.idleEffect.update(control);
                    control.update();
                }
                break;
            case MenuState.ANIM_EXIT:
                for (MenuControl control : _controls) {
                    control.outEffect.update(control);
                }
                break;
        }
    }

    @Override
    public void render(GraphicEngine ge) {
        super.render(ge);
        for (MenuControl control : _controls) {
            if (control.visible) {
                control.render(ge);
            }
        }
    }

    private ArrayList<MenuControl> _controls = new ArrayList<MenuControl>(5);
}
