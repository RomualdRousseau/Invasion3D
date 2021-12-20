package com.rworld.core.states.menu.effects;

import com.rworld.core.states.menu.MenuControl;
import com.rworld.core.states.menu.MenuEffect;

public class BlinkIdleEffect extends MenuEffect {

    public BlinkIdleEffect(float time) {
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
        if (_t > _time) {
            menuControl.visible = !menuControl.visible;
            _t = 0.0f;
        }
        return true;
    }

    private float _time;
    private float _t = 0.0f;
    private boolean _init = false;
}
