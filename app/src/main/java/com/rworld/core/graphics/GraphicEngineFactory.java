package com.rworld.core.graphics;

import com.rworld.core.GameActivity;
import com.rworld.core.graphics.high.GraphicEngineImpl;

public class GraphicEngineFactory {

    public static GraphicEngine createInstance(GameActivity activity) {
        return new GraphicEngineImpl(activity);
    }
}
