package opendoja.probes;

import com.nttdocomo.ui.IApplication;
import opendoja.host.DoJaEncoding;
import opendoja.host.JamLauncher;
import opendoja.host.LaunchConfig;

import java.nio.file.Files;
import java.nio.file.Path;

public final class JamMetadataEncodingProbe {
    private static final String EXPECTED_APP_NAME = "\u30c6\u30b9\u30c8\u65e5\u672c\u8a9e";

    private JamMetadataEncodingProbe() {
    }

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("jam-metadata-encoding");
        Path jam = root.resolve("EncodingProbe.jam");
        Files.writeString(jam, buildJam(), DoJaEncoding.DEFAULT_CHARSET);

        LaunchConfig config = JamLauncher.buildLaunchConfig(jam, false);
        check(EXPECTED_APP_NAME.equals(config.title()), "AppName should decode with the DoJa charset");
        check(EXPECTED_APP_NAME.equals(config.parameters().get("AppName")),
                "AppName parameter should preserve Japanese text");

        System.out.println("Jam metadata encoding probe OK");
    }

    private static String buildJam() {
        return "AppClass=" + ProbeApp.class.getName() + '\n'
                + "AppName=" + EXPECTED_APP_NAME + '\n'
                + "DrawArea=176x208\n";
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
