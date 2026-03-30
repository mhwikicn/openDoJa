package opendoja.probes;

import com.nttdocomo.ui.IApplication;
import opendoja.host.DoJaRuntime;
import opendoja.host.JamLauncher;
import opendoja.host.LaunchConfig;
import opendoja.host.OpenDoJaLog;

import javax.microedition.io.Connector;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class JamScratchpadProbe {
    private JamScratchpadProbe() {
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        OpenDoJaLog.configure(OpenDoJaLog.Level.WARN);

        verifySiblingScratchpadWrite();
        verifyParentSpFallbackWrite();
        verifyMissingScratchpadWarningAndCreation();

        System.out.println("Jam scratchpad probe OK");
    }

    private static void verifySiblingScratchpadWrite() throws Exception {
        Path root = Files.createTempDirectory("jam-sp-sibling");
        Path jam = root.resolve("Sibling.jam");
        Path scratchpad = root.resolve("Sibling.sp");
        Files.write(scratchpad, new byte[0]);
        writeJam(jam, null);

        LaunchConfig config = JamLauncher.buildLaunchConfig(jam, false);
        check(scratchpad.equals(config.scratchpadPackedFile()), "sibling .sp should be preferred");

        launchAndWrite(jam, "ok");
        check("ok".equals(Files.readString(scratchpad, StandardCharsets.UTF_8)), "sibling .sp should receive direct writes");
    }

    private static void verifyParentSpFallbackWrite() throws Exception {
        Path root = Files.createTempDirectory("jam-sp-fallback");
        Path bin = Files.createDirectories(root.resolve("bin"));
        Path sp = Files.createDirectories(root.resolve("sp"));
        Path jam = bin.resolve("Fallback.jam");
        Path scratchpad = sp.resolve("Fallback.sp");
        Files.write(scratchpad, new byte[0]);
        writeJam(jam, null);

        LaunchConfig config = JamLauncher.buildLaunchConfig(jam, false);
        check(scratchpad.equals(config.scratchpadPackedFile()), "../sp fallback should be selected when sibling is absent");

        launchAndWrite(jam, "fb");
        check("fb".equals(Files.readString(scratchpad, StandardCharsets.UTF_8)), "../sp .sp should receive direct writes");
    }

    private static void verifyMissingScratchpadWarningAndCreation() throws Exception {
        Path root = Files.createTempDirectory("jam-sp-missing");
        Path jam = root.resolve("Missing.jam");
        Path scratchpad = root.resolve("Missing.sp");
        writeJam(jam, "SPsize=4\n");

        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        LaunchConfig config;
        try (PrintStream capture = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            config = JamLauncher.buildLaunchConfig(jam, false);
        } finally {
            System.setOut(originalOut);
        }

        check(scratchpad.equals(config.scratchpadPackedFile()), "missing .sp should target the sibling path");
        check(captured.toString(StandardCharsets.UTF_8).contains("No .sp file found"),
                "missing .sp should log a warning");

        launchAndWrite(jam, "init");
        check(Files.exists(scratchpad), "missing .sp should be creatable at runtime");
        byte[] bytes = Files.readAllBytes(scratchpad);
        check(bytes.length == 68, "SPsize-backed .sp should use a packed header layout");
        check(Arrays.equals("init".getBytes(StandardCharsets.UTF_8), Arrays.copyOfRange(bytes, 64, 68)),
                "runtime writes should land in the created packed .sp payload");
    }

    private static void launchAndWrite(Path jam, String value) throws Exception {
        IApplication app = JamLauncher.launch(jam, false);
        try (OutputStream out = Connector.openOutputStream("scratchpad:///0:pos=0")) {
            out.write(value.getBytes(StandardCharsets.UTF_8));
        } finally {
            DoJaRuntime runtime = DoJaRuntime.current();
            if (runtime != null) {
                runtime.shutdown();
            }
        }
        if (app == null) {
            throw new IllegalStateException("Jam launch returned null application");
        }
    }

    private static void writeJam(Path jam, String extraProperties) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("AppClass=").append(ProbeApp.class.getName()).append('\n');
        builder.append("AppName=Scratchpad Probe\n");
        if (extraProperties != null) {
            builder.append(extraProperties);
        }
        Files.writeString(jam, builder.toString(), StandardCharsets.ISO_8859_1);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static final class ProbeApp extends IApplication {
        @Override
        public void start() {
        }
    }
}
