package com.rworld.core.states.menu.effects;

import com.rworld.core.states.menu.MenuControl;
import com.rworld.core.states.menu.MenuEffect;

public class HideOutEffect extends MenuEffect {

    public HideOutEffect(float time) {
    }

    @Override
    public boolean update(MenuControl menuControl) {
        menuControl.visible = false;
        return false;
    }
}
