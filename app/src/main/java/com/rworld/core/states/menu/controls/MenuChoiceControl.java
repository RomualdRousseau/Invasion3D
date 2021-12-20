package com.rworld.core.states.menu.controls;

import com.rworld.core.GameActivity;
import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.states.StyleProperty;
import com.rworld.core.states.menu.MenuControl;

import java.util.ArrayList;

public class MenuChoiceControl extends MenuControl {

    public StyleProperty style = new StyleProperty();
    public OnTouchListener onTouchListener;

    public int selectedIndex = -1;

    public ChoiceItem getSelectedItem() {
        return _items.get(selectedIndex);
    }

    public void addItem(ChoiceItem newItem) {
        _items.add(newItem);
        selectedIndex = 0;
    }

    @Override
    public void update() {
        super.update();
        GameActivity activity = menuState.activity;
        if (activity.isTouched()) {
            _buttonTouched = layout.contains(activity.getTouchX(), activity.getTouchY(), _offset);
            if (_buttonTouched && (onTouchListener != null)) {
                onTouchListener.onTouchPress(this);
            }
        } else if (_buttonTouched) {
            selectedIndex = (selectedIndex + 1) % _items.size();
            if (onTouchListener != null) {
                onTouchListener.onTouchRelease(this);
            }
            _buttonTouched = false;
        }
    }

    @Override
    public void render(GraphicEngine ge) {
        super.render(ge);
        String text = (selectedIndex >= 0) ? _items.get(selectedIndex).text : null;
        if ((text != null) && (text != "")) {
            int textX = 0;
            int textY = (int) ((layout.height - menuState.internalFont.getHeight() * style.fontScale) / 2 + menuState.internalFont.getBaseLine() * style.fontScale);

            if (style.align == StyleProperty.ALIGN_CENTER) {
                textX = (int) ((layout.width - style.padding * 2 - menuState.internalFont.getWidth() * style.fontScale * text.length()) / 2);
            } else if (style.align == StyleProperty.ALIGN_RIGHT) {
                textX = (int) (layout.width - style.padding * 2 - menuState.internalFont.getWidth() * style.fontScale * text.length());
            }

            if (_buttonTouched) {
                ge.gl.glColor4f(style.touchedColor[0], style.touchedColor[1], style.touchedColor[2], style.touchedColor[3]);
            } else {
                ge.gl.glColor4f(style.color[0], style.color[1], style.color[2], style.color[3]);
            }
            ge.drawText2D(menuState.internalFont, _offset[0] + layout.x + style.padding + textX, _offset[1] + layout.y + textY, style.fontScale, text);
            ge.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    private boolean _buttonTouched = false;
    private ArrayList<ChoiceItem> _items = new ArrayList<ChoiceItem>();
}
