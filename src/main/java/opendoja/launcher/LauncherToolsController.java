package opendoja.launcher;

import java.awt.Component;

final class LauncherToolsController {
    private final KeybindSettingsController keybindSettingsController = new KeybindSettingsController();

    void showKeybindSettings(Component parent) {
        keybindSettingsController.showDialog(parent);
    }
}
