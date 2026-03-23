package com.nttdocomo.opt.ui;

import com.nttdocomo.ui.PhoneSystem;
import opendoja.host.DoJaRuntime;

/**
 * Defines additional optional device attributes.
 */
public class PhoneSystem2 extends PhoneSystem {
    public static final int DEV_INDICATOR_BACKGROUND = 128;
    public static final int DEV_MELODY_VOLUME = 129;
    public static final int DEV_SE_VOLUME = 130;
    public static final int DEV_ILLUMINATION = 133;
    public static final int DEV_MEMO_LED = 134;
    public static final int DEV_KEY_SLANT = 135;
    public static final int DEV_DISPLAY_BRIGHTNESS = 136;
    public static final int DEV_SUBDISPLAY_BRIGHTNESS = 137;
    public static final int DEV_DISPLAY_CONTRAST = 138;
    public static final int DEV_SUBDISPLAY_CONTRAST = 139;
    public static final int DEV_ALLOCATABLE_JAVA_MEMORY = 140;
    public static final int DEV_DISPLAY_STYLE = 142;
    public static final int ATTR_VOLUME_MIN = 0;
    public static final int ATTR_VOLUME_MAX = 100;
    public static final int ATTR_ILLUMINATION_OFF = 0;
    public static final int ATTR_ILLUMINATION_WHITE = 1;
    public static final int ATTR_ILLUMINATION_ORANGE = 2;
    public static final int ATTR_ILLUMINATION_YELLOW = 3;
    public static final int ATTR_ILLUMINATION_GREEN = 4;
    public static final int ATTR_ILLUMINATION_SKYBLUE = 5;
    public static final int ATTR_ILLUMINATION_BLUE = 6;
    public static final int ATTR_ILLUMINATION_VIOLET = 7;
    public static final int ATTR_ILLUMINATION_RAINBOW = 8;
    public static final int ATTR_ILLUMINATION_GRADUALLY = 0x01000000;
    public static final int ATTR_MEMO_LED_OFF = 0;
    public static final int ATTR_MEMO_LED_ON = -1;
    public static final int ATTR_KEY_SLANT_OFF = 0;
    public static final int ATTR_KEY_SLANT_ON = 1;
    public static final int ATTR_BRIGHTNESS_MIN = 0;
    public static final int ATTR_BRIGHTNESS_MAX = 255;
    public static final int ATTR_CONTRAST_MIN = 0;
    public static final int ATTR_CONTRAST_MAX = 255;
    public static final int ATTR_DISPLAY_STYLE_VERTICAL = 0;
    public static final int ATTR_DISPLAY_STYLE_HORIZONTAL_RIGHT = 1;
    public static final int ATTR_DISPLAY_STYLE_HORIZONTAL_LEFT = 2;
    public static final int ATTR_DISPLAY_STYLE_REVERSE = 3;

    static {
        resetRuntimeDefaults();
    }

    public static void resetRuntimeDefaults() {
        PhoneSystem.setAttribute(DEV_MELODY_VOLUME, ATTR_VOLUME_MAX);
        PhoneSystem.setAttribute(DEV_SE_VOLUME, ATTR_VOLUME_MAX);
        PhoneSystem.setAttribute(DEV_ILLUMINATION, ATTR_ILLUMINATION_OFF);
        PhoneSystem.setAttribute(DEV_MEMO_LED, ATTR_MEMO_LED_OFF);
        PhoneSystem.setAttribute(DEV_KEY_SLANT, ATTR_KEY_SLANT_OFF);
        PhoneSystem.setAttribute(DEV_DISPLAY_BRIGHTNESS, ATTR_BRIGHTNESS_MAX);
        PhoneSystem.setAttribute(DEV_SUBDISPLAY_BRIGHTNESS, ATTR_BRIGHTNESS_MAX);
        PhoneSystem.setAttribute(DEV_DISPLAY_CONTRAST, ATTR_CONTRAST_MAX);
        PhoneSystem.setAttribute(DEV_SUBDISPLAY_CONTRAST, ATTR_CONTRAST_MAX);
        PhoneSystem.setAttribute(DEV_DISPLAY_STYLE, defaultDisplayStyle());
        PhoneSystem.setAttribute(DEV_ALLOCATABLE_JAVA_MEMORY,
                (int) java.lang.Math.min(Integer.MAX_VALUE, Runtime.getRuntime().maxMemory()));
    }

    private static int defaultDisplayStyle() {
        DoJaRuntime runtime = DoJaRuntime.current();
        if (runtime != null && runtime.displayWidth() > runtime.displayHeight()) {
            return ATTR_DISPLAY_STYLE_HORIZONTAL_RIGHT;
        }
        return ATTR_DISPLAY_STYLE_VERTICAL;
    }
}
