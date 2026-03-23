package com.nttdocomo.opt.ui;

import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.PhoneSystem;

/**
 * Defines a canvas that can request a display style.
 */
public abstract class Canvas2 extends Canvas {
    public static final int CANVAS_STYLE_VERTICAL = 0;
    public static final int CANVAS_STYLE_HORIZONTAL_RIGHT = 1;
    public static final int CANVAS_STYLE_HORIZONTAL_LEFT = 2;

    private final int style;

    /**
     * Creates a canvas using the vertical style.
     */
    public Canvas2() {
        this(CANVAS_STYLE_VERTICAL);
    }

    /**
     * Creates a canvas with the specified display style.
     *
     * @param style the display style
     */
    public Canvas2(int style) {
        if (style < CANVAS_STYLE_VERTICAL || style > CANVAS_STYLE_HORIZONTAL_LEFT) {
            throw new IllegalArgumentException("style");
        }
        this.style = style;
        PhoneSystem.setAttribute(PhoneSystem2.DEV_DISPLAY_STYLE, style);
    }

    int style() {
        return style;
    }
}
