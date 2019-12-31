package com.rworld.core.states.menu;

import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.graphics.Surface;
import com.rworld.core.graphics.Texture;
import com.rworld.core.states.LayoutProperty;
import com.rworld.core.states.MenuState;
import com.rworld.core.utilities.Utility;

public abstract class MenuControl {

	public final LayoutProperty layout = new LayoutProperty();
	public Surface backgroundSurface;
	public MenuEffect inEffect = MenuEffect.None;
	public MenuEffect idleEffect = MenuEffect.None;
	public MenuEffect outEffect = MenuEffect.None;
	public boolean visible = true;
	
	public MenuState menuState;
	public MenuControl parent;
	
	
	public void init(MenuState menuState, MenuControl parent) {
		this.menuState = menuState;
		this.parent = parent;
	}
	
	public void dispose(GraphicEngine ge) {
		backgroundSurface = Utility.safeDispose(backgroundSurface, ge.gl);
	}
	
	public void pause() {
	}
	
	public void resume() {
	}
	
	public void update() {
		offsetFromParents(_offset);
	}

	public void render(GraphicEngine ge) {
		offsetFromParents(_offset);
		if(backgroundSurface != null) {
			ge.drawSurface2D(backgroundSurface, _offset[0] + layout.x, _offset[1] + layout.y, layout.width, layout.height);
		}
	}
	
	public void loadTextures(GraphicEngine ge, Texture texture) {
		if(backgroundSurface != null) {
			backgroundSurface.setTexture(texture);
		}
	}
	
	private void offsetFromParents(int[] offset){
		offset[0] = 0;
		offset[1] = 0;
		MenuControl p = parent;
		while(p != null) {
			offset[0] += parent.layout.x;
			offset[1] += parent.layout.y;
			p = parent.parent;
		}
	}

	protected int[] _offset = {0, 0};
}
