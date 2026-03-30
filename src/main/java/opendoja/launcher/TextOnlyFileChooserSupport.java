package opendoja.launcher;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;
import java.awt.Component;
import java.awt.Graphics;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

final class TextOnlyFileChooserSupport {
    private static final Icon EMPTY_ICON = new Icon() {
        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
        }

        @Override
        public int getIconWidth() {
            return 0;
        }

        @Override
        public int getIconHeight() {
            return 0;
        }
    };

    private static final String[] ICON_KEYS = {
            "FileView.directoryIcon",
            "FileView.fileIcon",
            "FileView.computerIcon",
            "FileView.hardDriveIcon",
            "FileView.floppyDriveIcon",
            "FileChooser.homeFolderIcon",
            "FileChooser.newFolderIcon",
            "FileChooser.upFolderIcon",
            "FileChooser.listViewIcon",
            "FileChooser.detailsViewIcon"
    };

    Path chooseJamFile(Component parent, Path initialDirectory) {
        Map<String, Object> previousValues = overrideIcons();
        try {
            JFileChooser chooser = initialDirectory == null
                    ? new JFileChooser()
                    : new JFileChooser(initialDirectory.toFile());
            chooser.setDialogTitle("Load JAM");
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileFilter(new FileNameExtensionFilter("JAM files (*.jam)", "jam"));
            chooser.setFileView(new FileView() {
                @Override
                public Icon getIcon(java.io.File file) {
                    return EMPTY_ICON;
                }
            });
            int result = chooser.showOpenDialog(parent);
            if (result != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
                return null;
            }
            return chooser.getSelectedFile().toPath().toAbsolutePath().normalize();
        } finally {
            restoreIcons(previousValues);
        }
    }

    private static Map<String, Object> overrideIcons() {
        Map<String, Object> previousValues = new LinkedHashMap<>();
        for (String key : ICON_KEYS) {
            previousValues.put(key, UIManager.get(key));
            UIManager.put(key, EMPTY_ICON);
        }
        return previousValues;
    }

    private static void restoreIcons(Map<String, Object> previousValues) {
        for (Map.Entry<String, Object> entry : previousValues.entrySet()) {
            UIManager.put(entry.getKey(), entry.getValue());
        }
    }
}
