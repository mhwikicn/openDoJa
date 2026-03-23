package com.nttdocomo.ui;

import com.nttdocomo.opt.ui.PhoneSystem2;
import com.nttdocomo.system.StoreException;
import opendoja.host.DoJaRuntime;
import opendoja.host.system.DoJaSystemRegistry;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Defines the handset device system.
 * This class provides access to platform-native resources and theme settings.
 */
public class PhoneSystem {
    public static final int ATTR_BACKLIGHT_OFF = 0;
    public static final int ATTR_BACKLIGHT_ON = 1;
    public static final int ATTR_FOLDING_CLOSE = 0;
    public static final int ATTR_FOLDING_OPEN = 1;
    public static final int ATTR_MAIL_AT_CENTER = 2;
    public static final int ATTR_MAIL_NONE = 0;
    public static final int ATTR_MAIL_RECEIVED = 1;
    public static final int ATTR_MESSAGE_AT_CENTER = 2;
    public static final int ATTR_MESSAGE_NONE = 0;
    public static final int ATTR_MESSAGE_RECEIVED = 1;
    public static final int ATTR_VIBRATOR_OFF = 0;
    public static final int ATTR_VIBRATOR_ON = 1;
    public static final int ATTR_BATTERY_PARTIAL = 0;
    public static final int ATTR_BATTERY_FULL = 1;
    public static final int ATTR_BATTERY_CHARGING = 2;
    public static final int ATTR_SERVICEAREA_OUTSIDE = 0;
    public static final int ATTR_SERVICEAREA_INSIDE = 1;
    public static final int ATTR_MANNER_OFF = 0;
    public static final int ATTR_MANNER_ON = 1;
    public static final int ATTR_SCREEN_INVISIBLE = 0;
    public static final int ATTR_SCREEN_VISIBLE = 1;
    public static final int ATTR_SURROUND_OFF = 0;
    public static final int ATTR_SURROUND_ON = 1;
    public static final int ATTR_AREAINFO_FOMA = 0;
    public static final int ATTR_AREAINFO_HSDPA = 1;
    public static final int ATTR_AREAINFO_OUTSIDE = 2;
    public static final int ATTR_AREAINFO_ROAMINGOUT = 3;
    public static final int ATTR_AREAINFO_SELFMODE = 4;
    public static final int ATTR_AREAINFO_COMMUNICATING = 5;
    public static final int ATTR_AREAINFO_UNKNOWN = 99;
    public static final int DEV_BACKLIGHT = 0;
    public static final int DEV_VIBRATOR = 1;
    public static final int DEV_FOLDING = 2;
    public static final int DEV_MAILBOX = 3;
    public static final int DEV_MESSAGEBOX = 4;
    public static final int DEV_BATTERY = 5;
    public static final int DEV_SERVICEAREA = 6;
    public static final int DEV_MANNER = 7;
    public static final int DEV_KEYPAD = 8;
    public static final int DEV_SCREEN_VISIBLE = 9;
    public static final int DEV_AUDIO_SURROUND = 10;
    public static final int DEV_AREAINFO = 11;
    public static final int MIN_VENDOR_ATTR = 64;
    public static final int MAX_VENDOR_ATTR = 127;
    public static final int MAX_OPTION_ATTR = 255;
    public static final int MIN_OPTION_ATTR = 128;
    public static final int SOUND_INFO = 0;
    public static final int SOUND_WARNING = 1;
    public static final int SOUND_ERROR = 2;
    public static final int SOUND_ALARM = 3;
    public static final int SOUND_CONFIRM = 4;
    public static final int THEME_STANDBY = 0;
    public static final int THEME_CALL_OUT = 1;
    public static final int THEME_CALL_IN = 2;
    public static final int THEME_MESSAGE_SEND = 3;
    public static final int THEME_MESSAGE_RECEIVE = 4;
    public static final int THEME_AV_CALL_IN = 5;
    public static final int THEME_CHAT_RECEIVED = 6;
    public static final int THEME_AV_CALLING = 7;

    private static final Map<Integer, Integer> ATTRIBUTES = new HashMap<>();

    static {
        ATTRIBUTES.put(DEV_BACKLIGHT, ATTR_BACKLIGHT_ON);
        ATTRIBUTES.put(DEV_VIBRATOR, ATTR_VIBRATOR_OFF);
        ATTRIBUTES.put(DEV_FOLDING, ATTR_FOLDING_OPEN);
        ATTRIBUTES.put(DEV_MAILBOX, ATTR_MAIL_NONE);
        ATTRIBUTES.put(DEV_MESSAGEBOX, ATTR_MESSAGE_NONE);
        ATTRIBUTES.put(DEV_BATTERY, ATTR_BATTERY_FULL);
        ATTRIBUTES.put(DEV_SERVICEAREA, ATTR_SERVICEAREA_INSIDE);
        ATTRIBUTES.put(DEV_MANNER, ATTR_MANNER_OFF);
        ATTRIBUTES.put(DEV_SCREEN_VISIBLE, ATTR_SCREEN_VISIBLE);
        ATTRIBUTES.put(DEV_AUDIO_SURROUND, ATTR_SURROUND_OFF);
        ATTRIBUTES.put(DEV_AREAINFO, ATTR_AREAINFO_FOMA);
    }

    protected PhoneSystem() {
    }

    /**
     * Controls a native resource attribute.
     * If a nonexistent or non-controllable resource type is specified, the request is ignored.
     *
     * @param device the native resource type
     * @param attribute the attribute value to set
     * @throws IllegalArgumentException if an invalid attribute value is specified for a valid controllable resource
     */
    public static void setAttribute(int device, int attribute) {
        if (!isKnownAttribute(device)) {
            return;
        }
        if (!isControllableAttribute(device)) {
            return;
        }
        if (!isValidAttributeValue(device, attribute)) {
            throw new IllegalArgumentException("Invalid attribute value " + attribute + " for device " + device);
        }
        ATTRIBUTES.put(device, attribute);
    }

    /**
     * Gets the attribute of a native resource.
     *
     * @param device the native resource type
     * @return the attribute value, or {@code -1} if the resource type does not exist
     */
    public static int getAttribute(int device) {
        return ATTRIBUTES.getOrDefault(device, -1);
    }

    /**
     * Gets whether the native resource is controllable.
     *
     * @param device the native resource type
     * @return {@code true} if the resource is controllable, otherwise {@code false}
     */
    public static boolean isAvailable(int device) {
        return isKnownAttribute(device) && isControllableAttribute(device);
    }

    /**
     * Plays a standard sound.
     *
     * @param sound the standard sound type
     * @throws IllegalArgumentException if {@code sound} is invalid
     */
    public static void playSound(int sound) {
        if (sound < SOUND_INFO || sound > SOUND_CONFIRM) {
            throw new IllegalArgumentException("sound out of range: " + sound);
        }
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Sets a still image, video-only movie, or avatar theme shown for calls or similar native events.
     *
     * @param theme the theme target type
     * @param value the entry ID from {@code ImageStore}, {@code MovieStore}, or {@code AvatarStore}
     * @throws IllegalStateException if the application is inactive
     * @throws IllegalArgumentException if the target is not valid for the specified entry kind
     * @throws StoreException if the entry does not exist
     */
    public static void setImageTheme(int theme, int value) throws StoreException {
        ensureThemeOperationAllowed();
        DoJaSystemRegistry.EntryKind kind = requireThemeEntry(value,
                DoJaSystemRegistry.EntryKind.IMAGE,
                DoJaSystemRegistry.EntryKind.MOVIE,
                DoJaSystemRegistry.EntryKind.AVATAR);
        if (!isValidImageThemeTarget(theme, kind)) {
            throw new IllegalArgumentException("Unsupported image theme target " + theme + " for " + kind);
        }
        DoJaSystemRegistry.setImageTheme(theme, value);
    }

    /**
     * Sets a sound or audio-only movie theme.
     *
     * @param theme the theme target type
     * @param value the entry ID from {@code SoundStore} or {@code MovieStore}
     * @throws IllegalStateException if the application is inactive
     * @throws IllegalArgumentException if the target is not valid for the specified entry kind
     * @throws StoreException if the entry does not exist
     */
    public static void setSoundTheme(int theme, int value) throws StoreException {
        ensureThemeOperationAllowed();
        DoJaSystemRegistry.EntryKind kind = requireThemeEntry(value,
                DoJaSystemRegistry.EntryKind.SOUND,
                DoJaSystemRegistry.EntryKind.MOVIE);
        if (!isValidSoundThemeTarget(theme, kind)) {
            throw new IllegalArgumentException("Unsupported sound theme target " + theme + " for " + kind);
        }
        DoJaSystemRegistry.setSoundTheme(theme, value);
    }

    /**
     * Sets a movie theme that contains both video and audio.
     *
     * @param theme the theme target type
     * @param value the entry ID from {@code MovieStore}
     * @throws IllegalStateException if the application is inactive
     * @throws IllegalArgumentException if the target is invalid
     * @throws StoreException if the entry does not exist
     */
    public static void setMovieTheme(int theme, int value) throws StoreException {
        ensureThemeOperationAllowed();
        requireThemeEntry(value, DoJaSystemRegistry.EntryKind.MOVIE);
        if (!isValidMovieThemeTarget(theme)) {
            throw new IllegalArgumentException("Unsupported movie theme target: " + theme);
        }
        DoJaSystemRegistry.setMovieTheme(theme, value);
    }

    /**
     * Sets submenu icon images in one operation.
     *
     * @param menuIds the path to the menu hierarchy whose icons are to be changed
     * @param iconIds the image entry IDs to assign; {@code -1} leaves an icon unchanged
     * @throws NullPointerException if {@code menuIds} or {@code iconIds} is {@code null}
     * @throws IllegalStateException if the application is inactive
     * @throws IllegalArgumentException if the specified path is invalid or unsupported
     * @throws StoreException if any specified image entry does not exist
     */
    public static void setMenuIcons(int[] menuIds, int[] iconIds) throws StoreException {
        ensureThemeOperationAllowed();
        Objects.requireNonNull(menuIds, "path");
        Objects.requireNonNull(iconIds, "ids");
        for (int menuId : menuIds) {
            if (menuId < 0) {
                throw new IllegalArgumentException("Negative menu path element: " + menuId);
            }
        }
        if (menuIds.length != 0) {
            throw new IllegalArgumentException("Only the top menu hierarchy is supported on the desktop host");
        }
        for (int iconId : iconIds) {
            if (iconId == -1) {
                continue;
            }
            requireThemeEntry(iconId, DoJaSystemRegistry.EntryKind.IMAGE);
        }
        DoJaSystemRegistry.setMenuIcons(menuIds, iconIds);
    }

    private static void ensureThemeOperationAllowed() {
        DoJaRuntime runtime = DoJaRuntime.current();
        if (runtime != null && runtime.application() instanceof MApplication application && !application.isActive()) {
            throw new IllegalStateException("Application is inactive");
        }
    }

    private static DoJaSystemRegistry.EntryKind requireThemeEntry(int id, DoJaSystemRegistry.EntryKind... allowedKinds)
            throws StoreException {
        DoJaSystemRegistry.requireEntry(id, allowedKinds);
        return DoJaSystemRegistry.entryKind(id);
    }

    private static boolean isValidImageThemeTarget(int theme, DoJaSystemRegistry.EntryKind kind) {
        return switch (kind) {
            case IMAGE -> theme == THEME_STANDBY
                    || theme == THEME_CALL_OUT
                    || theme == THEME_CALL_IN
                    || theme == THEME_MESSAGE_SEND
                    || theme == THEME_MESSAGE_RECEIVE
                    || theme == THEME_AV_CALL_IN;
            case MOVIE -> theme == THEME_CALL_IN || theme == THEME_AV_CALL_IN;
            case AVATAR -> theme == THEME_AV_CALLING;
            default -> false;
        };
    }

    private static boolean isValidSoundThemeTarget(int theme, DoJaSystemRegistry.EntryKind kind) {
        return switch (kind) {
            case SOUND -> theme == THEME_CALL_IN
                    || theme == THEME_MESSAGE_RECEIVE
                    || theme == THEME_AV_CALL_IN
                    || theme == THEME_CHAT_RECEIVED;
            case MOVIE -> theme == THEME_CALL_IN || theme == THEME_AV_CALL_IN;
            default -> false;
        };
    }

    private static boolean isValidMovieThemeTarget(int theme) {
        return theme == THEME_STANDBY
                || theme == THEME_CALL_IN
                || theme == THEME_AV_CALL_IN
                || theme == THEME_MESSAGE_RECEIVE;
    }

    private static boolean isKnownAttribute(int device) {
        return (device >= DEV_BACKLIGHT && device <= DEV_AREAINFO)
                || isKnownOptionalAttribute(device);
    }

    private static boolean isControllableAttribute(int device) {
        return device == DEV_BACKLIGHT
                || device == DEV_VIBRATOR
                || isKnownOptionalAttribute(device);
    }

    private static boolean isValidAttributeValue(int device, int attribute) {
        return switch (device) {
            case DEV_BACKLIGHT -> attribute == ATTR_BACKLIGHT_OFF || attribute == ATTR_BACKLIGHT_ON;
            case DEV_VIBRATOR -> attribute == ATTR_VIBRATOR_OFF || attribute == ATTR_VIBRATOR_ON;
            case PhoneSystem2.DEV_MELODY_VOLUME, PhoneSystem2.DEV_SE_VOLUME -> attribute >= 0 && attribute <= 100;
            case PhoneSystem2.DEV_ILLUMINATION -> {
                int baseColor = attribute & 0x00FFFFFF;
                int flags = attribute & 0xFF000000;
                yield baseColor >= 0
                        && baseColor <= 8
                        && (flags == 0 || flags == PhoneSystem2.ATTR_ILLUMINATION_GRADUALLY);
            }
            case PhoneSystem2.DEV_MEMO_LED -> attribute == PhoneSystem2.ATTR_MEMO_LED_OFF
                    || attribute == PhoneSystem2.ATTR_MEMO_LED_ON;
            case PhoneSystem2.DEV_KEY_SLANT -> attribute == PhoneSystem2.ATTR_KEY_SLANT_OFF
                    || attribute == PhoneSystem2.ATTR_KEY_SLANT_ON;
            case PhoneSystem2.DEV_DISPLAY_BRIGHTNESS, PhoneSystem2.DEV_SUBDISPLAY_BRIGHTNESS,
                    PhoneSystem2.DEV_DISPLAY_CONTRAST, PhoneSystem2.DEV_SUBDISPLAY_CONTRAST -> attribute >= 0 && attribute <= 255;
            case PhoneSystem2.DEV_ALLOCATABLE_JAVA_MEMORY -> attribute >= 0;
            case PhoneSystem2.DEV_DISPLAY_STYLE -> attribute >= PhoneSystem2.ATTR_DISPLAY_STYLE_VERTICAL
                    && attribute <= PhoneSystem2.ATTR_DISPLAY_STYLE_REVERSE;
            default -> true;
        };
    }

    private static boolean isKnownOptionalAttribute(int device) {
        return device == PhoneSystem2.DEV_MELODY_VOLUME
                || device == PhoneSystem2.DEV_SE_VOLUME
                || device == PhoneSystem2.DEV_ILLUMINATION
                || device == PhoneSystem2.DEV_MEMO_LED
                || device == PhoneSystem2.DEV_KEY_SLANT
                || device == PhoneSystem2.DEV_DISPLAY_BRIGHTNESS
                || device == PhoneSystem2.DEV_SUBDISPLAY_BRIGHTNESS
                || device == PhoneSystem2.DEV_DISPLAY_CONTRAST
                || device == PhoneSystem2.DEV_SUBDISPLAY_CONTRAST
                || device == PhoneSystem2.DEV_ALLOCATABLE_JAVA_MEMORY
                || device == PhoneSystem2.DEV_DISPLAY_STYLE;
    }
}
