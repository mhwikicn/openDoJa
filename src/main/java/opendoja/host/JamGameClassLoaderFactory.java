package opendoja.host;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

final class JamGameClassLoaderFactory {
    private JamGameClassLoaderFactory() {
    }

    static URLClassLoader create(Path gameJarPath, ClassLoader parent) {
        if (gameJarPath == null) {
            throw new IllegalArgumentException("gameJarPath must not be null");
        }
        try {
            String encodedFileUrl = gameJarPath.toAbsolutePath().normalize().toUri().toASCIIString().replace("!", "%21");
            return new URLClassLoader(new URL[]{new URL(encodedFileUrl)}, parent);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Could not create class loader for " + gameJarPath, e);
        }
    }
}
