package opendoja.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

final class LauncherProcessSupport {
    Process startInBackground(GameLaunchSelection selection) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(buildLaunchCommand(selection));
        if (selection.jamPath().getParent() != null) {
            processBuilder.directory(selection.jamPath().getParent().toFile());
        }
        processBuilder.inheritIO();
        return processBuilder.start();
    }

    int runInForeground(GameLaunchSelection selection) throws IOException, InterruptedException {
        Process process = startInBackground(selection);
        return process.waitFor();
    }

    List<String> buildLaunchCommand(GameLaunchSelection selection) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        appendForwardedProperties(command, System.getProperties());
        command.add("-cp");
        command.add(resolveLauncherArtifact() + File.pathSeparator + selection.gameJarPath());
        command.add(OpenDoJaLauncher.class.getName());
        command.add(OpenDoJaLauncher.internalRunJamFlag());
        command.add(selection.jamPath().toString());
        return command;
    }

    private void appendForwardedProperties(List<String> command, Properties properties) {
        for (String name : properties.stringPropertyNames()) {
            if (name.startsWith("opendoja.") || name.equals("java.awt.headless") || name.equals("sun.java2d.uiScale")) {
                command.add("-D" + name + "=" + properties.getProperty(name));
            }
        }
    }

    private Path resolveLauncherArtifact() throws IOException {
        try {
            URL location = OpenDoJaLauncher.class.getProtectionDomain().getCodeSource().getLocation();
            if (location != null) {
                return Path.of(location.toURI()).toAbsolutePath().normalize();
            }
        } catch (URISyntaxException e) {
            throw new IOException("Could not resolve launcher location", e);
        }
        throw new IOException("Could not resolve launcher location");
    }
}
