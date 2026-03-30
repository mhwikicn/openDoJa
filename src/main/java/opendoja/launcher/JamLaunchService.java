package opendoja.launcher;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.IOException;
import java.nio.file.Path;

final class JamLaunchService {
    private final JamGameJarResolver gameJarResolver = new JamGameJarResolver();
    private final LauncherProcessSupport processSupport = new LauncherProcessSupport();
    private Path lastDirectory;

    Path chooseJamFile(Component parent) {
        JFileChooser chooser = lastDirectory == null
                ? new JFileChooser()
                : new JFileChooser(lastDirectory.toFile());
        chooser.setDialogTitle("Load JAM");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("JAM files (*.jam)", "jam"));
        int result = chooser.showOpenDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
            return null;
        }
        Path jamPath = chooser.getSelectedFile().toPath().toAbsolutePath().normalize();
        lastDirectory = jamPath.getParent();
        return jamPath;
    }

    GameLaunchResult launchInSeparateProcess(Path jamPath) throws IOException {
        GameLaunchSelection selection = gameJarResolver.resolve(jamPath);
        Process process = processSupport.startInBackground(selection);
        return new GameLaunchResult(selection, process.pid());
    }
}
