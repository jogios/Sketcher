package org.sketcher.style;

import java.util.HashMap;
import java.util.Map;

import org.sketcher.Style;

public class StylesFactory {
	public static final short SKETCHY = 0x1001;
	public static final short SHADED = 0x1002;
	public static final short CHROME = 0x1003;
	public static final short FUR = 0x1004;
	public static final short LONGFUR = 0x1005;
	public static final short WEB = 0x1006;
	public static final short SQUARES = 0x1007;
	public static final short RIBBON = 0x1008;
	public static final short CIRCLES = 0x1009;
	public static final short GRID = 0x1010;

	private static Map<Integer, Style> cache = new HashMap<Integer, Style>();
	private static int currentStyle = SKETCHY;

	public static Style getStyle(int id) {
		if (!cache.containsKey(id)) {
			cache.put(id, getStyleInstance(id));
		}
		currentStyle = id;
		return cache.get(id);
	}

	public static Style getCurrentStyle() {
		return getStyle(currentStyle);
	}

	public static void clearCache() {
		cache.clear();
	}

	private static Style getStyleInstance(int id) {
		switch (id) {
		case SKETCHY:
			return new SketchyStyle();
		case SHADED:
			return new ShadedStyle();
		case CHROME:
			return new ChromeStyle();
		case FUR:
			return new FurStyle();
		case LONGFUR:
			return new LongfurStyle();
		case WEB:
			return new WebStyle();
		case SQUARES:
			return new SquaresStyle();
		case RIBBON:
			return new RibbonStyle();
		case CIRCLES:
			return new CirclesStyle();
		case GRID:
			return new GridStyle();

		default:
			throw new RuntimeException("Invalid style ID");
		}
	}
}
