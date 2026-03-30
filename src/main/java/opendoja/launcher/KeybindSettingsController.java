package opendoja.launcher;

import javax.swing.JOptionPane;
import java.awt.Component;

final class KeybindSettingsController {
    void showDialog(Component parent) {
        JOptionPane.showMessageDialog(
                parent,
                "Keybind settings are not implemented yet.",
                "Keybind Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
