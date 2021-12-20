package com.rworld.core.states.menu.effects;

import com.rworld.core.states.menu.MenuControl;
import com.rworld.core.states.menu.MenuEffect;

public class HideInEffect extends MenuEffect {

    public HideInEffect(float time) {
        _time = time;
    }

    @Override
    public boolean update(MenuControl menuControl) {
        if (!_init) {
            menuControl.visible = false;
            _t = 0.0f;
            _init = true;
        } else {
            _t += menuControl.menuState.time;
        }
        if (_t <= _time) {
            return true;
        } else {
            menuControl.visible = true;
            return false;
        }
    }

    private float _time;
    private boolean _init = false;
    private float _t = 0.0f;
}
