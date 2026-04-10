package opendoja.host;

import java.lang.management.ManagementFactory;

/**
 * Shared JVM file-encoding policy for child launches and compatibility re-exec.
 */
public final class LaunchEncodingSupport {
    private static final String FILE_ENCODING_PREFIX = "-Dfile.encoding=";

    private LaunchEncodingSupport() {
    }

    public static String childProcessFileEncoding() {
        String explicit = explicitFileEncodingArgument();
        if (explicit != null) {
            return explicit;
        }
        return configuredDefaultEncoding();
    }

    public static boolean hasExplicitFileEncodingArgument() {
        return explicitFileEncodingArgument() != null;
    }

    public static String configuredDefaultEncoding() {
        String override = OpenDoJaLaunchArgs.get(OpenDoJaLaunchArgs.DEFAULT_ENCODING, null);
        if (override != null) {
            String value = override.trim();
            if (!value.isEmpty()) {
                return value;
            }
        }
        return DoJaEncoding.defaultCharsetName();
    }

    private static String explicitFileEncodingArgument() {
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.startsWith(FILE_ENCODING_PREFIX)) {
                String value = arg.substring(FILE_ENCODING_PREFIX.length()).trim();
                return value.isEmpty() ? null : value;
            }
        }
        return null;
    }
}
