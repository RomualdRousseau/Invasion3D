package com.rworld.core.states.menu.controls;

import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.states.StyleProperty;
import com.rworld.core.states.menu.MenuControl;

public class MenuLabelControl extends MenuControl {

    public String text = "Label";
    public StyleProperty style = new StyleProperty();

    @Override
    public void render(GraphicEngine ge) {
        super.render(ge);
        if ((text != null) && (text != "")) {
            int textX = 0;
            int textY = (int) ((layout.height - menuState.internalFont.getHeight() * style.fontScale) / 2 + menuState.internalFont.getBaseLine() * style.fontScale);

            if (style.align == StyleProperty.ALIGN_CENTER) {
                textX = (int) ((layout.width - style.padding * 2 - menuState.internalFont.getWidth() * style.fontScale * text.length()) / 2);
            } else if (style.align == StyleProperty.ALIGN_RIGHT) {
                textX = (int) (layout.width - style.padding * 2 - menuState.internalFont.getWidth() * style.fontScale * text.length());
            }

            ge.gl.glColor4f(style.color[0], style.color[1], style.color[2], style.color[3]);
            ge.drawText2D(menuState.internalFont, _offset[0] + layout.x + style.padding + textX, _offset[1] + layout.y + textY, style.fontScale, text);
            ge.gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
