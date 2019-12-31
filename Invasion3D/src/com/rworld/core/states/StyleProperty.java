package com.rworld.core.states;

public class StyleProperty {

		public final static int ALIGN_LEFT = 0;
		public final static int ALIGN_CENTER = 1;
		public final static int ALIGN_RIGHT = 2;
		
		public float[] color = {1.0f, 1.0f, 1.0f, 1.0f};
		public float[] touchedColor = {1.0f, 1.0f, 1.0f, 1.0f};
		public int align = ALIGN_LEFT;
		public int padding = 10;
		public float fontScale = 2.0f;
}
