package opendoja.launcher;

import opendoja.host.DoJaEncoding;

import java.util.List;

public final class LauncherProcessSupportProbe {
    private LauncherProcessSupportProbe() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            throw new IllegalArgumentException("Usage: LauncherProcessSupportProbe [expected-file-encoding]");
        }
        String expectedEncoding = args.length == 0 ? DoJaEncoding.defaultCharsetName() : args[0];
        verifyBuildLaunchCommandAddsExpectedFileEncoding(expectedEncoding);
        System.out.println("Launcher process support probe OK");
    }

    private static void verifyBuildLaunchCommandAddsExpectedFileEncoding(String expectedEncoding) throws Exception {
        GameLaunchSelection selection = new GameLaunchSelection(
                java.nio.file.Path.of("probe.jam"),
                java.nio.file.Path.of("probe.jar"));
        List<String> command = new LauncherProcessSupport().buildLaunchCommand(selection);
        String expectedArgument = "-Dfile.encoding=" + expectedEncoding;
        check(command.contains(expectedArgument),
                "launch command should contain " + expectedArgument + " but was " + command);
        check(command.stream().filter(arg -> arg.startsWith("-Dfile.encoding=")).count() == 1,
                "launch command should contain exactly one file.encoding argument: " + command);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message + " (default charset=" + DoJaEncoding.defaultCharsetName() + ")");
        }
    }
}
