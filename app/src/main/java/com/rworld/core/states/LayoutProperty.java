package com.rworld.core.states;

import com.rworld.core.maths.Rectangle2D;

public class LayoutProperty extends Rectangle2D {

    public boolean contains(int a, int b, int[] offset) {
        return (offset[0] + x <= a) && (a < offset[0] + x + width) && (offset[1] + y <= b) && (b < offset[1] + y + height);
    }
}
