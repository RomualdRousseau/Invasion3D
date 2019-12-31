package com.rworld.core.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.rworld.core.GameState;

public class PhysicalEntityCollection<T extends PhysicalEntity> extends ArrayList<T> {
	private static final long serialVersionUID = 2784911743477566690L;
	
	public PhysicalEntityCollection(IPhysicalEntityFactory<T> creator) {
		super();
		_creator = creator;
	}
	
	public PhysicalEntityCollection(IPhysicalEntityFactory<T> creator, Collection<? extends T> c) {
		super(c);
		_creator = creator;
	}
	
	public PhysicalEntityCollection(IPhysicalEntityFactory<T> creator, int initialCapacity) {
		super(initialCapacity);
		_creator = creator;
	}
	
	public final T spawn() {
		T e = (!_gc.isEmpty()) ? _gc.removeFirst() : _creator.getNewInstance();
		this.add(e);
		return e;
	}
	
	public void updateAll(GameState gameState) {
		final int s = this.size();
		int n = s;
		for(int i = 0; i < n;) {
			final T e = this.get(i);
			if(e.life > 0.0f) {
				e.update(gameState);
				i++;
			}
			else {
				_gc.addFirst(e);
				this.set(i, this.get(--n));
			}
		}
		if(n < s) {
			this.removeRange(n, s);
		}
	}

	private IPhysicalEntityFactory<T> _creator;
	private LinkedList<T> _gc = new LinkedList<T>();
}
