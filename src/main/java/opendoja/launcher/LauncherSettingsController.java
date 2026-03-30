package opendoja.launcher;

import opendoja.audio.mld.MldSynth;

import java.awt.Component;
import java.util.List;

final class LauncherSettingsController {
    private final KeybindSettingsController keybindSettingsController = new KeybindSettingsController();

    void showKeybinds(Component parent) {
        keybindSettingsController.showDialog(parent);
    }

    List<MldSynth> availableSynths() {
        return List.of(MldSynth.values());
    }
}
