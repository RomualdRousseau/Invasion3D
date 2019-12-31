package com.rworld.core.states.menu.controls;

import com.rworld.core.states.menu.MenuControl;

public interface OnTouchListener {
	
	public void onTouchPress(MenuControl menuControl);

	public void onTouchRelease(MenuControl menuControl);
}
