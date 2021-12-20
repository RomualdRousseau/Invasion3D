package com.rworld.core.states;

import com.rworld.core.GameActivity;
import com.rworld.core.GameState;
import com.rworld.core.graphics.GraphicEngine;

import java.util.ArrayList;

public abstract class SwitchState extends GameState {

    public int condition() {
        return _command;
    }

    public void addCase(int value, GameState state) {
        _cases.add(value, state);
    }

    public void breakCase(int command) {
        _command = command;
    }

    @Override
    public void init(GameActivity activity, GraphicEngine ge) {
        super.init(activity, ge);
        _command = -1;
    }

    @Override
    public GameState getNextState() {
        int c = condition();
        return (c >= 0) ? _cases.get(c) : this;
    }

    private int _command = -1;
    private ArrayList<GameState> _cases = new ArrayList<GameState>(5);
}
