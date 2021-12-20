package com.rworld.core.states.menu.effects;

import com.rworld.core.states.menu.MenuControl;
import com.rworld.core.states.menu.MenuEffect;

public class SlideLeftOutEffect extends MenuEffect {

    public SlideLeftOutEffect(float time) {
        _time = time + 0.1f;
    }

    @Override
    public boolean update(MenuControl menuControl) {
        if (!_init) {
            _x1 = menuControl.layout.x;
            _x2 = -menuControl.layout.width;
            _t = 0.0f;
            _init = true;
        } else {
            menuControl.layout.x = (int) ((_x2 - _x1) * Math.min(_t / _time, 1.0f) + _x1);
            _t += menuControl.menuState.time;
        }
        if (_t <= _time + 0.5f) {
            return true;
        } else {
            menuControl.layout.x = _x2;
            return false;
        }
    }

    private float _time;
    private boolean _init = false;
    private int _x1;
    private int _x2;
    private float _t;
}
