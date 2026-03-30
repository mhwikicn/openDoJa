package opendoja.launcher;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

final class JamLaunchService {
    private final JamGameJarResolver gameJarResolver = new JamGameJarResolver();
    private final LauncherProcessSupport processSupport = new LauncherProcessSupport();
    private final LauncherPreferencesStore preferencesStore;
    private final TextOnlyFileChooserSupport fileChooserSupport;

    JamLaunchService() {
        this(new LauncherPreferencesStore(), new TextOnlyFileChooserSupport());
    }

    JamLaunchService(LauncherPreferencesStore preferencesStore, TextOnlyFileChooserSupport fileChooserSupport) {
        this.preferencesStore = preferencesStore;
        this.fileChooserSupport = fileChooserSupport;
    }

    Path chooseJamFile(Component parent) {
        Path jamPath = fileChooserSupport.chooseJamFile(parent, preferencesStore.lastDirectory());
        if (jamPath != null) {
            preferencesStore.rememberLastDirectory(jamPath.getParent());
        }
        return jamPath;
    }

    GameLaunchResult launchInSeparateProcess(Path jamPath) throws IOException {
        GameLaunchSelection selection = gameJarResolver.resolve(jamPath);
        Process process = processSupport.startInBackground(selection, preferencesStore.loadSettings());
        preferencesStore.rememberLaunchedJam(selection.jamPath());
        return new GameLaunchResult(selection, process.pid());
    }

    List<Path> recentJamPaths() {
        return preferencesStore.recentJamPaths();
    }

    void removeRecentJam(Path jamPath) {
        preferencesStore.removeRecentJam(jamPath);
    }

    LauncherSettings loadSettings() {
        return preferencesStore.loadSettings();
    }

    void saveSettings(LauncherSettings settings) {
        preferencesStore.saveSettings(settings);
    }
}
